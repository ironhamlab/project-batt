/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_URL: string;
  readonly VITE_NAVER_MAP_CLIENT_ID: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

declare module "*.module.css" {
  const classes: { readonly [key: string]: string };
  export default classes;
}
declare module "*.css" {
  const classes: { readonly [key: string]: string };
  export default classes;
}

declare module "*.png" {
  const value: string;
  export default value;
}

declare module "*.jpg" {
  const value: string;
  export default value;
}

declare module "*.jpeg" {
  const value: string;
  export default value;
}

declare module "*.gif" {
  const value: string;
  export default value;
}

declare module "*.svg" {
  const value: string;
  export default value;
}
