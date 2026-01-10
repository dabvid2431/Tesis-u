#!/usr/bin/env python3
"""Simple checker for JaCoCo XML report that fails if LINE coverage < min_percent."""
import argparse
import sys
import xml.etree.ElementTree as ET

def main():
    p = argparse.ArgumentParser()
    p.add_argument("--xml", required=True, help="Path to jacoco XML report")
    p.add_argument("--min", type=float, default=75.0, help="Minimum required line coverage percent")
    args = p.parse_args()

    try:
        tree = ET.parse(args.xml)
    except Exception as e:
        print(f"ERROR: Can't parse XML report: {e}")
        sys.exit(2)

    root = tree.getroot()
    # Find counter element with type="LINE"
    line_counter = None
    for c in root.findall('.//counter'):
        if c.attrib.get('type') == 'LINE':
            line_counter = c
            break

    if line_counter is None:
        print("ERROR: LINE counter not found in JaCoCo report")
        sys.exit(2)

    missed = int(line_counter.attrib.get('missed', '0'))
    covered = int(line_counter.attrib.get('covered', '0'))
    total = missed + covered
    coverage = (covered / total * 100) if total > 0 else 0.0

    print(f"LINE coverage: {coverage:.2f}% (covered={covered} missed={missed})")

    if coverage + 1e-6 < args.min:
        print(f"Coverage {coverage:.2f}% is below minimum {args.min}%")
        sys.exit(1)

    print("Coverage threshold satisfied")
    sys.exit(0)

if __name__ == '__main__':
    main()
