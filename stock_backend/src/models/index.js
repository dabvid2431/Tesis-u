import { sequelize } from "../config/database.js";
import Category from "./category.model.js";
import Product from "./product.model.js";
import Supplier from "./supplier.model.js";
import Client from "./client.model.js";
import Purchase from "./purchase.model.js";
import PurchaseItem from "./purchaseItem.model.js";
import Sale from "./sale.model.js";
import SaleItem from "./saleItem.model.js";
import StockMovement from "./stockMovement.model.js";
import { Notification } from "./notification.model.js";

export {
  sequelize, Category, Product, Supplier, Client,
  Purchase, PurchaseItem, Sale, SaleItem, StockMovement, Notification
};
