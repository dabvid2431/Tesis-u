import js from "@eslint/js";
import globals from "globals";

export default [
  {
    // Aplicar a todos los archivos JavaScript
    files: ["**/*.js", "**/*.mjs", "**/*.cjs"],
    languageOptions: {
      ecmaVersion: "latest",
      sourceType: "module",
      globals: {
        ...globals.node,    // Soluciona el error 'process is not defined'
        ...globals.jest,    // Soluciona los errores 'describe', 'test', 'expect'
      },
    },
    rules: {
      ...js.configs.recommended.rules,
      "no-unused-vars": "warn", // Cambia error por advertencia para que no bloquee
    },
  },
];