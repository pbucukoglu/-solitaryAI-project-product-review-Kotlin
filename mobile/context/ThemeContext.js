import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { useColorScheme } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { createTheme } from '../components/theme';

export const ThemeContext = createContext();

export const useTheme = () => useContext(ThemeContext);

const STORAGE_KEYS = {
  themePreference: 'settings.themePreference',
  fontScale: 'settings.fontScale',
};

export const ThemeProvider = ({ children }) => {
  const systemScheme = useColorScheme() || 'light';
  const [themePreference, setThemePreferenceState] = useState('system');
  const [fontScale, setFontScaleState] = useState(1);
  const [hydrated, setHydrated] = useState(false);

  useEffect(() => {
    let cancelled = false;

    const hydrate = async () => {
      try {
        const storedThemePreference = await AsyncStorage.getItem(STORAGE_KEYS.themePreference);
        const storedFontScale = await AsyncStorage.getItem(STORAGE_KEYS.fontScale);

        if (!cancelled) {
          if (storedThemePreference === 'light' || storedThemePreference === 'dark' || storedThemePreference === 'system') {
            setThemePreferenceState(storedThemePreference);
          }

          const parsed = storedFontScale ? Number(storedFontScale) : NaN;
          if (!Number.isNaN(parsed) && parsed > 0.7 && parsed < 1.6) {
            setFontScaleState(parsed);
          }

          setHydrated(true);
        }
      } catch (e) {
        if (!cancelled) setHydrated(true);
      }
    };

    hydrate();
    return () => {
      cancelled = true;
    };
  }, []);

  const effectiveScheme = themePreference === 'system' ? systemScheme : themePreference;
  const theme = useMemo(() => createTheme(effectiveScheme), [effectiveScheme]);

  const setThemePreference = async (nextPreference) => {
    const value = nextPreference === 'light' || nextPreference === 'dark' || nextPreference === 'system'
      ? nextPreference
      : 'system';
    setThemePreferenceState(value);
    try {
      await AsyncStorage.setItem(STORAGE_KEYS.themePreference, value);
    } catch (e) {
      // ignore
    }
  };

  const setFontScale = async (nextFontScale) => {
    const value = typeof nextFontScale === 'number' ? nextFontScale : 1;
    const clamped = Math.max(0.8, Math.min(1.4, value));
    setFontScaleState(clamped);
    try {
      await AsyncStorage.setItem(STORAGE_KEYS.fontScale, String(clamped));
    } catch (e) {
      // ignore
    }
  };

  const contextValue = useMemo(
    () => ({
      theme,
      themePreference,
      setThemePreference,
      fontScale,
      scaleFont: (size) => {
        const n = Number(size);
        if (!Number.isFinite(n)) return size;
        return n * (fontScale || 1);
      },
      setFontScale,
      hydrated,
    }),
    [theme, themePreference, fontScale, hydrated]
  );

  return <ThemeContext.Provider value={contextValue}>{children}</ThemeContext.Provider>;
};
