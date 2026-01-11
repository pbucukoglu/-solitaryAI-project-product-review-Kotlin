import AsyncStorage from '@react-native-async-storage/async-storage';
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

import en from '../locales/en.json';
import tr from '../locales/tr.json';
import es from '../locales/es.json';

export const APP_LANGUAGE_STORAGE_KEY = 'appLanguage';

const SUPPORTED_LANGS = ['en', 'tr', 'es'];

const normalizeLang = (lang) => {
  const base = (lang || '').toLowerCase().split('-')[0];
  return SUPPORTED_LANGS.includes(base) ? base : 'en';
};

export const setAppLanguage = async (lang) => {
  const normalized = normalizeLang(lang);
  await AsyncStorage.setItem(APP_LANGUAGE_STORAGE_KEY, normalized);
  await i18n.changeLanguage(normalized);
};

const init = async () => {
  const stored = await AsyncStorage.getItem(APP_LANGUAGE_STORAGE_KEY);
  const initial = stored ? normalizeLang(stored) : 'en';
  if (stored == null) {
    await AsyncStorage.setItem(APP_LANGUAGE_STORAGE_KEY, initial);
  }
  await i18n.changeLanguage(initial);
};

i18n
  .use(initReactI18next)
  .init({
    compatibilityJSON: 'v4',
    resources: {
      en: { translation: en },
      tr: { translation: tr },
      es: { translation: es },
    },
    lng: 'en',
    fallbackLng: 'en',
    interpolation: {
      escapeValue: false,
    },
  });

init().catch(() => {
  // ignore and keep fallback
});

export default i18n;
