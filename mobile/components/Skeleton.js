import React, { useEffect, useMemo } from 'react';
import { Animated, Easing, View } from 'react-native';

const Skeleton = ({
  width = '100%',
  height = 12,
  radius = 8,
  style,
  baseColor = 'rgba(0,0,0,0.08)',
  highlightColor = 'rgba(0,0,0,0.14)',
}) => {
  const animated = useMemo(() => new Animated.Value(0), []);

  useEffect(() => {
    const loop = Animated.loop(
      Animated.sequence([
        Animated.timing(animated, {
          toValue: 1,
          duration: 900,
          easing: Easing.inOut(Easing.quad),
          useNativeDriver: false,
        }),
        Animated.timing(animated, {
          toValue: 0,
          duration: 900,
          easing: Easing.inOut(Easing.quad),
          useNativeDriver: false,
        }),
      ])
    );

    loop.start();
    return () => loop.stop();
  }, [animated]);

  const backgroundColor = animated.interpolate({
    inputRange: [0, 1],
    outputRange: [baseColor, highlightColor],
  });

  return (
    <Animated.View
      style={[
        {
          width,
          height,
          borderRadius: radius,
          backgroundColor,
        },
        style,
      ]}
    />
  );
};

export const SkeletonRow = ({ children, style }) => {
  return (
    <View style={[{ flexDirection: 'row', alignItems: 'center' }, style]}>
      {children}
    </View>
  );
};

export default Skeleton;
