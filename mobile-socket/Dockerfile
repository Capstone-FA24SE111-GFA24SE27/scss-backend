# Stage 1: Build Stage
FROM node:18 AS builder

# Tạo thư mục làm việc trong container
WORKDIR /usr/src/app

# Sao chép package.json và package-lock.json
COPY package*.json ./

# Cài đặt các phụ thuộc
RUN npm install

# Sao chép toàn bộ mã nguồn vào thư mục làm việc
COPY . .

# Stage 2: Production Stage
FROM node:18-slim

# Tạo thư mục làm việc trong container
WORKDIR /usr/src/app

# Sao chép file package.json và package-lock.json từ build stage
COPY --from=builder /usr/src/app/package*.json ./

# Cài đặt các phụ thuộc trong production
RUN npm install --only=production

# Sao chép toàn bộ mã nguồn từ build stage
COPY --from=builder /usr/src/app .

# Sao chép file .env vào container
COPY .env .env

# Expose port mà ứng dụng của bạn sẽ chạy
EXPOSE 3000

# Chạy ứng dụng khi container khởi động
CMD ["node", "app.js"]
