# Jenkins 빌드를 고려한 프론트엔드 (build 폴더 복사 방식)
FROM nginx:alpine

# Nginx 설정 복사
COPY nginx.conf /etc/nginx/nginx.conf

# Jenkins에서 빌드된 정적 파일 복사
COPY dist/ /usr/share/nginx/html/

# 포트 노출
EXPOSE 80

# Nginx 실행
CMD ["nginx", "-g", "daemon off;"]