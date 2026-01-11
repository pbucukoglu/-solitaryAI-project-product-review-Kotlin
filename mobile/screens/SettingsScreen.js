import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import Constants from 'expo-constants';
import { useTranslation } from 'react-i18next';
import i18n from '../i18n';
import { setAppLanguage } from '../i18n';
import { useTheme } from '../context/ThemeContext';

const SettingsScreen = ({ navigation }) => {
  const { theme, themePreference, setThemePreference, fontScale, setFontScale, scaleFont } = useTheme();
  const { t } = useTranslation();
  const [selectedTheme, setSelectedTheme] = useState(themePreference || 'system');
  const [selectedFontScale, setSelectedFontScale] = useState(fontScale || 1);
  const [selectedLanguage, setSelectedLanguage] = useState((i18n.language || 'en').split('-')[0]);

  const appName = Constants?.expoConfig?.name || Constants?.manifest?.name || 'Claro';
  const appVersion = Constants?.expoConfig?.version || Constants?.manifest?.version;

  useEffect(() => {
    setSelectedTheme(themePreference || 'system');
  }, [themePreference]);

  useEffect(() => {
    setSelectedFontScale(fontScale || 1);
  }, [fontScale]);

  useEffect(() => {
    setSelectedLanguage((i18n.language || 'en').split('-')[0]);
  }, [i18n.language]);

  const applyTheme = async (value) => {
    setSelectedTheme(value);
    await setThemePreference(value);
  };

  const applyFontScale = async (value) => {
    setSelectedFontScale(value);
    await setFontScale(value);
  };

  const applyLanguage = async (value) => {
    setSelectedLanguage(value);
    await setAppLanguage(value);
  };

  const themeOptions = [
    { key: 'system', label: t('settings.systemDefault'), icon: 'phone-portrait-outline' },
    { key: 'light', label: t('settings.light'), icon: 'sunny-outline' },
    { key: 'dark', label: t('settings.dark'), icon: 'moon-outline' },
  ];

  const fontOptions = [
    { key: 'small', label: t('settings.small'), value: 0.9 },
    { key: 'medium', label: t('settings.medium'), value: 1.0 },
    { key: 'large', label: t('settings.large'), value: 1.15 },
  ];

  const languageOptions = [
    { key: 'en', label: t('languageNames.en') },
    { key: 'tr', label: t('languageNames.tr') },
    { key: 'es', label: t('languageNames.es') },
  ];

  const isFontSelected = (value) => Math.abs((selectedFontScale || 1) - value) < 0.01;

  return (
    <ScrollView style={[styles.container, { backgroundColor: theme.colors.background }]}>
      <View style={[styles.content, { backgroundColor: theme.colors.surface }]}>
        <View style={styles.section}>
          <Text style={[styles.sectionTitle, { color: theme.colors.text }]}>
            {t('settings.appearance')}
          </Text>

          <View style={styles.optionGroup}>
            <Text style={[styles.label, { color: theme.colors.textSecondary, fontSize: scaleFont(16) }]}>{t('settings.theme')}</Text>
            {themeOptions.map((option) => {
              const selected = selectedTheme === option.key;
              return (
                <TouchableOpacity
                  key={option.key}
                  style={[
                    styles.optionRow,
                    {
                      backgroundColor: theme.colors.surfaceAlt,
                      borderColor: selected ? theme.colors.primary : theme.colors.border,
                    },
                  ]}
                  onPress={() => applyTheme(option.key)}
                >
                  <View style={styles.optionRowLeft}>
                    <Ionicons name={option.icon} size={18} color={theme.colors.textSecondary} />
                    <Text style={[styles.optionText, { color: theme.colors.text, fontSize: scaleFont(16) }]}>{option.label}</Text>
                  </View>
                  {selected && <Ionicons name="checkmark" size={20} color={theme.colors.primary} />}
                </TouchableOpacity>
              );
            })}
          </View>

          <View style={styles.optionGroup}>
            <Text style={[styles.label, { color: theme.colors.textSecondary, fontSize: scaleFont(16) }]}>{t('settings.fontSize')}</Text>
            {fontOptions.map((option) => {
              const selected = isFontSelected(option.value);
              return (
                <TouchableOpacity
                  key={option.key}
                  style={[
                    styles.optionRow,
                    {
                      backgroundColor: theme.colors.surfaceAlt,
                      borderColor: selected ? theme.colors.primary : theme.colors.border,
                    },
                  ]}
                  onPress={() => applyFontScale(option.value)}
                >
                  <View style={styles.optionRowLeft}>
                    <Ionicons name="text-outline" size={18} color={theme.colors.textSecondary} />
                    <Text style={[styles.optionText, { color: theme.colors.text, fontSize: scaleFont(16) }]}>{option.label}</Text>
                  </View>
                  {selected && <Ionicons name="checkmark" size={20} color={theme.colors.primary} />}
                </TouchableOpacity>
              );
            })}
          </View>

          <View style={styles.optionGroup}>
            <Text style={[styles.label, { color: theme.colors.textSecondary, fontSize: scaleFont(16) }]}>{t('settings.language')}</Text>
            {languageOptions.map((option) => {
              const selected = selectedLanguage === option.key;
              return (
                <TouchableOpacity
                  key={option.key}
                  style={[
                    styles.optionRow,
                    {
                      backgroundColor: theme.colors.surfaceAlt,
                      borderColor: selected ? theme.colors.primary : theme.colors.border,
                    },
                  ]}
                  onPress={() => applyLanguage(option.key)}
                >
                  <View style={styles.optionRowLeft}>
                    <Ionicons name="language-outline" size={18} color={theme.colors.textSecondary} />
                    <Text style={[styles.optionText, { color: theme.colors.text, fontSize: scaleFont(16) }]}>{option.label}</Text>
                  </View>
                  {selected && <Ionicons name="checkmark" size={20} color={theme.colors.primary} />}
                </TouchableOpacity>
              );
            })}
          </View>
        </View>

        <View style={styles.section}>
          <Text style={[styles.sectionTitle, { color: theme.colors.text }]}>
            {t('settings.about')}
          </Text>
          <Text style={[styles.aboutText, { color: theme.colors.textSecondary }]}>
            {appName}{appVersion ? ` v${appVersion}` : ''}
          </Text>
          <Text style={[styles.aboutText, { color: theme.colors.textSecondary }]}>
            {t('settings.demoFallback')}
          </Text>
        </View>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  content: {
    flex: 1,
    margin: 16,
    borderRadius: 12,
    padding: 20,
  },
  section: {
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '600',
    marginBottom: 16,
  },
  label: {
    fontWeight: '500',
    marginBottom: 8,
  },
  optionGroup: {
    marginBottom: 18,
  },
  optionRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 12,
    paddingHorizontal: 12,
    borderRadius: 10,
    borderWidth: 1,
    marginBottom: 10,
  },
  optionRowLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
  },
  optionText: {
    fontWeight: '600',
  },
  aboutText: {
    fontSize: 14,
    lineHeight: 20,
    marginBottom: 8,
  },
});

export default SettingsScreen;
