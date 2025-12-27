FROM node:22-alpine as build

WORKDIR /build


COPY . .

RUN npm ci
RUN npm run build

#No vamos a ejecutar el cliente con Node, usaremos Nginx
FROM nginx:latest

COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /build/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
