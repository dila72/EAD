import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  output: 'standalone', // Required for Docker deployment
  
  // Disable ESLint during build for Docker
  eslint: {
    ignoreDuringBuilds: true,
  },
  
  // Disable TypeScript errors during build
  typescript: {
    ignoreBuildErrors: true,
  },
  
  // Optional: Add image optimization configuration if needed
  images: {
    unoptimized: process.env.NODE_ENV === 'production' ? false : true,
  },
};

export default nextConfig;
