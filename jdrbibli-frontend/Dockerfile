# Étape 1 : Build Angular
FROM node:20 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build -- --configuration production

# Étape 2 : Serve avec Nginx
FROM nginx:alpine

# Copie du build Angular
COPY --from=build /app/dist/jdrbibli-frontend/browser/ /usr/share/nginx/html/

# Copie de la configuration Nginx personnalisée
COPY default.conf /etc/nginx/conf.d/default.conf
# Copie des pages d'erreur Nginx
COPY src/nginx-errors/50x.html /usr/share/nginx/html/

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
