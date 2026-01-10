import { Sequelize } from "sequelize";
import dotenv from "dotenv";
dotenv.config();

// Use a separate database for tests to avoid polluting the development DB
const dbName = process.env.NODE_ENV === 'test'
  ? (process.env.TEST_DB_NAME || `${process.env.DB_NAME}_test`)
  : process.env.DB_NAME;

export const sequelize = new Sequelize(
  dbName,
  process.env.DB_USER,
  process.env.DB_PASS,
  {
    host: process.env.DB_HOST,
    port: process.env.DB_PORT,
    dialect: "postgres",
    logging: false
  }
);
