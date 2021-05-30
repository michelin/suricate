FROM nginx AS base
COPY docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY docker/frontend/replace_api_url.sh /

FROM node:12-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json angular.json tsconfig.json ./
COPY src/main/webapp src/main/webapp
RUN npm install --no-progress
RUN npm run build
RUN ls -al target/dist/public

FROM base
COPY --from=builder /app/target/dist/public /usr/share/nginx/html
EXPOSE 80
CMD ["sh", "replace_api_url.sh"]
