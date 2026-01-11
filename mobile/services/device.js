import AsyncStorage from '@react-native-async-storage/async-storage';

const STORAGE_KEY = 'device_id_v1';

const generateId = () => {
  const rand = Math.random().toString(16).slice(2);
  const time = Date.now().toString(16);
  return `dev-${time}-${rand}`;
};

export const deviceService = {
  getDeviceId: async () => {
    const existing = await AsyncStorage.getItem(STORAGE_KEY);
    if (existing) return existing;
    const id = generateId();
    await AsyncStorage.setItem(STORAGE_KEY, id);
    return id;
  },
};
