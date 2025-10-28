# Étape 1 : Build Angular
FROM node:20 AS build
WORKDIR /app

COPY package*.json ./
RUN npm install

# Copie du code source
COPY . .

# 🧠 Vérification : s’assurer que le code du composant contient bien “uploads/images”
RUN echo "=== Vérif: contenu ouvrage-detail et ouvrage-ami avant build ===" \
 && grep -R "uploads/images" /app/src/app/pages/ouvrage-detail-page/ || echo "❌ PAS DE uploads/images dans ouvrage-detail-page.ts" \
 && grep -R "uploads/images" /app/src/app/pages/ouvrage-ami-detail-page/ || echo "❌ PAS DE uploads/images dans ouvrage-ami-detail-page.ts"

# Build Angular en mode production
RUN npm run build -- --configuration production

# Étape 2 : Serve avec Nginx
FROM nginx:alpine

# Copie du build Angular
COPY --from=build /app/dist/jdrbibli-frontend/browser/ /usr/share/nginx/html/

# Copie de la configuration Nginx personnalisée
COPY default.conf /etc/nginx/conf.d/default.conf

# Copie des pages d'erreur
COPY src/nginx-errors/50x.html /usr/share/nginx/html/

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
