FROM node:22-alpine AS build

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM nginx:1.29.7-alpine

ENV API_UPSTREAM=http://host.docker.internal:8080

COPY nginx.conf /etc/nginx/templates/default.conf.template
COPY --from=build /app/dist/e-commerce-ng/browser /usr/share/nginx/html/ui

EXPOSE 80

CMD ["/bin/sh", "-c", "envsubst '$API_UPSTREAM' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf && exec nginx -g 'daemon off;'"]