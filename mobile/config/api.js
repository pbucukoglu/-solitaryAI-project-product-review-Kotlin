// API Configuration
// This file automatically detects the environment and sets the correct API base URL
import { Platform } from 'react-native';
import Constants from 'expo-constants';

// ============================================================================
// CONFIGURATION INSTRUCTIONS:
// ============================================================================
// 
// For Android Emulator (default): No changes needed - uses 10.0.2.2:8080
// 
// For Physical Device (Expo Go): 
//   1. Find your computer's IP: Windows (ipconfig) or Mac/Linux (ifconfig)
//   2. Replace 'YOUR_COMPUTER_IP' below with your actual IP (e.g., '192.168.1.100')
//   3. Make sure phone and computer are on same Wi-Fi network
//
// For Production APK:
//   1. Update app.json: Set "extra.apiUrl" to your production backend URL
//   2. Rebuild APK: eas build --platform android --profile production
//
// ============================================================================

// Determine API base URL based on environment
const getApiBaseUrl = () => {
  // Prefer app.json extra.apiUrl in ALL environments so emulator + APK behave identically
  const apiUrl = Constants.expoConfig?.extra?.apiUrl;
  if (apiUrl && !apiUrl.includes('YOUR_')) {
    return apiUrl;
  }
  
  // Final fallback
  console.warn('⚠️  Using localhost fallback. Set app.json extra.apiUrl to your backend URL.');
  return 'http://localhost:8080';
};

export const API_BASE_URL = getApiBaseUrl();

export const API_ENDPOINTS = {
  PRODUCTS: '/api/products',
  REVIEWS: '/api/reviews',
  TRANSLATE: '/api/translate',
};

// Log the API URL in development for debugging
if (__DEV__) {
  console.log('API Base URL:', API_BASE_URL);
}


