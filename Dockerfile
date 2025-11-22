FROM node:18-alpine
WORKDIR /usr/src/app
COPY stock_backend/package*.json ./
RUN npm ci --only=production
COPY stock_backend/ ./
ENV NODE_ENV=production
EXPOSE 3000
CMD ["node", "src/server.js"]
