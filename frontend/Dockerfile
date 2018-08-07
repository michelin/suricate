## Build image
FROM nginx

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY dist/ /usr/share/nginx/html/
COPY dist/ /usr/share/nginx/html/
COPY dist/assets/ /usr/share/nginx/html/assets/
COPY dist/assets/styles /usr/share/nginx/html/assets/styles

EXPOSE 80 
