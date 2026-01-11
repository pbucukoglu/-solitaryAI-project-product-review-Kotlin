import { Appearance } from 'react-native';

export const createTheme = (scheme) => {
  const isDark = scheme === 'dark';
  return {
    isDark,
    colors: {
      background: isDark ? '#0b0f17' : '#f5f5f5',
      surface: isDark ? '#121a27' : '#ffffff',
      surfaceAlt: isDark ? '#0f1622' : '#f9f9f9',
      text: isDark ? '#f2f4f7' : '#1c1c1e',
      textSecondary: isDark ? '#a7b0c0' : '#666666',
      border: isDark ? 'rgba(255,255,255,0.08)' : '#e6e6e6',
      primary: '#6200ee',
      warning: isDark ? '#f59e0b' : '#b45309',
      danger: '#d32f2f',
      shadow: isDark ? '#000000' : '#000000',
      overlay: 'rgba(0,0,0,0.6)',
      barTrack: isDark ? 'rgba(255,255,255,0.08)' : '#ececec',
      barFill: '#6200ee',
      offline: isDark ? '#f59e0b' : '#b45309',
    },
    spacing: {
      xs: 6,
      sm: 10,
      md: 16,
      lg: 20,
      xl: 28,
    },
    radius: {
      sm: 8,
      md: 12,
      lg: 16,
    },
  };
};

export const getSystemTheme = () => createTheme(Appearance.getColorScheme() || 'light');
