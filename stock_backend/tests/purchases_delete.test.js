import request from 'supertest';
import app from '../src/app.js';
import { sequelize, Product, StockMovement, Purchase } from '../src/models/index.js';

describe('Purchases DELETE behavior', () => {
  beforeAll(async () => {
    await sequelize.sync({ force: true });
    // seed category and supplier and product
    const cat = await (await import('../src/models/index.js')).Category.create({ name: 'DelCat', description: 'c' });
    const sup = await (await import('../src/models/index.js')).Supplier.create({ name: 'DelSup', email: 'del@sup.com' });
    await Product.create({ sku: 'D-001', name: 'DelProd', brand: 'D', purchasePrice: 2.0, salePrice: 3.0, stock: 0, categoryId: cat.id });
  }, 20000);

  afterAll(async () => {
    await sequelize.close();
  });

  test('Delete purchase reverts product stock and creates OUT stock movement', async () => {
    // create purchase for product id 1
    const createRes = await request(app)
      .post('/api/purchases')
      .send({ supplierId: 1, items: [{ productId: 1, quantity: 5, price: 2.0 }] });

    expect([200,201]).toContain(createRes.status);
    const purchaseId = createRes.body.id;

    // confirm product stock increased
    const prodBefore = await Product.findByPk(1);
    expect(prodBefore.stock).toBe(5);

    // delete purchase
    const delRes = await request(app).delete(`/api/purchases/${purchaseId}`);
    expect(delRes.status).toBe(204);

    // product stock should be reverted to 0
    const prodAfter = await Product.findByPk(1);
    expect(prodAfter.stock).toBe(0);

    // an OUT stock movement should exist referencing DeletePurchase:<id>
    const sm = await StockMovement.findOne({ where: { productId: 1, reference: `DeletePurchase:${purchaseId}`, type: 'OUT' } });
    expect(sm).not.toBeNull();

    // purchase and items should be removed
    const p = await Purchase.findByPk(purchaseId);
    expect(p).toBeNull();
  }, 20000);
});