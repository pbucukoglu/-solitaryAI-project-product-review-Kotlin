import React, { useEffect, useState } from 'react';
import { Animated, StyleSheet, Text, View } from 'react-native';
import NetInfo from '@react-native-community/netinfo';
import { useTheme } from '../context/ThemeContext';

const OfflineBanner = ({ theme }) => {
  const { scaleFont } = useTheme();
  const [visible, setVisible] = useState(false);
  const translateY = React.useRef(new Animated.Value(-60)).current;

  useEffect(() => {
    const unsubscribe = NetInfo.addEventListener((state) => {
      const isOffline = !(state.isConnected && state.isInternetReachable !== false);
      setVisible(isOffline);
    });

    return () => unsubscribe();
  }, []);

  useEffect(() => {
    Animated.timing(translateY, {
      toValue: visible ? 0 : -60,
      duration: 220,
      useNativeDriver: true,
    }).start();
  }, [visible, translateY]);

  return (
    <Animated.View
      pointerEvents="none"
      style={[
        styles.container,
        {
          transform: [{ translateY }],
          backgroundColor: theme?.colors?.surface || '#ffffff',
          borderColor: theme?.colors?.border || '#e6e6e6',
          shadowColor: theme?.colors?.shadow || '#000',
        },
      ]}
    >
      <View style={styles.inner}>
        <View
          style={[
            styles.dot,
            { backgroundColor: theme?.colors?.offline || '#b45309' },
          ]}
        />
        <Text
          style={[
            styles.text,
            { color: theme?.colors?.textSecondary || '#666', fontSize: scaleFont(13) },
          ]}
        >
          You are offline
        </Text>
      </View>
    </Animated.View>
  );
};

const styles = StyleSheet.create({
  container: {
    position: 'absolute',
    top: 10,
    left: 12,
    right: 12,
    zIndex: 50,
    borderWidth: 1,
    borderRadius: 14,
    shadowOffset: { width: 0, height: 10 },
    shadowOpacity: 0.14,
    shadowRadius: 18,
    elevation: 6,
  },
  inner: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 14,
    paddingVertical: 10,
  },
  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    marginRight: 10,
  },
  text: {
    fontSize: 13,
    fontWeight: '700',
  },
});

export default OfflineBanner;
