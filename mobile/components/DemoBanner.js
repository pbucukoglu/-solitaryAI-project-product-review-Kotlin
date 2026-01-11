import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { useTheme } from '../context/ThemeContext';

const DemoBanner = ({ onTryAgain }) => {
  const { theme, scaleFont } = useTheme();

  return (
    <View
      style={[
        styles.container,
        {
          backgroundColor: theme.colors.warning,
          shadowColor: theme.colors.shadow,
        },
      ]}
    >
      <Text style={[styles.text, { color: theme.colors.surface, fontSize: scaleFont(13) }]} numberOfLines={2}>
        Demo mode — showing sample data (offline)
      </Text>
      <TouchableOpacity onPress={onTryAgain} style={styles.tryAgainButton}>
        <Text style={[styles.tryAgainText, { color: theme.colors.surface, fontSize: scaleFont(12) }]}>
          Try again
        </Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 14,
    paddingVertical: 10,
    minHeight: 44,
    borderRadius: 14,
    marginHorizontal: 12,
    marginTop: 10,
    shadowOffset: { width: 0, height: 10 },
    shadowOpacity: 0.18,
    shadowRadius: 18,
    elevation: 6,
  },
  text: {
    fontSize: 13,
    fontWeight: '700',
    flex: 1,
  },
  tryAgainButton: {
    paddingHorizontal: 12,
    paddingVertical: 7,
    borderRadius: 16,
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    marginLeft: 12,
  },
  tryAgainText: {
    fontSize: 12,
    fontWeight: '600',
  },
});

export default DemoBanner;
