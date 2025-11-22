import request from 'supertest';
import app from '../src/app.js';
import { sequelize, Category, Supplier, Product } from '../src/models/index.js';

describe('Purchases API (integration)', () => {
  beforeAll(async () => {
    // recrear esquema limpio para tests
    await sequelize.sync({ force: true });
    // crear datos base
    const cat = await Category.create({ name: 'TestCat', description: 'cat' });
    const sup = await Supplier.create({ name: 'TestSupplier', email: 'test@supp.com' });
    await Product.create({ sku: 'T-001', name: 'TestProd', brand: 'T', purchasePrice: 1.00, salePrice: 2.00, stock: 0, categoryId: cat.id });
  }, 20000);

  afterAll(async () => {
    await sequelize.close();
  });

  test('Create purchase with valid payload returns 201', async () => {
    const res = await request(app)
      .post('/api/purchases')
      .send({ supplierId: 1, items: [{ productId: 1, quantity: 3, price: 1.0 }] });
    expect([200,201]).toContain(res.status);
    expect(res.body).toBeDefined();
    expect(res.body.total).toBeDefined();
  });

  test('Reject purchase with invalid item quantity', async () => {
    const res = await request(app)
      .post('/api/purchases')
      .send({ supplierId: 1, items: [{ productId: 1, quantity: 0, price: 1.0 }] });
    expect([400,422]).toContain(res.status);
  });
});
