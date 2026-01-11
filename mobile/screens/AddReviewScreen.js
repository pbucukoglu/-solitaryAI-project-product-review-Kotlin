import React, { useMemo, useRef, useState } from 'react';
import {
  Animated,
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Alert,
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { reviewService } from '../services/api';
import { deviceService } from '../services/device';
import { useTranslation } from 'react-i18next';

import OfflineBanner from '../components/OfflineBanner';
import { useTheme } from '../context/ThemeContext';

const AddReviewScreen = ({ route, navigation }) => {
  const { productId } = route.params;
  const { theme } = useTheme();
  const { t } = useTranslation();

  const [comment, setComment] = useState('');
  const [rating, setRating] = useState(5);
  const [reviewerName, setReviewerName] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const starScales = useRef([0, 1, 2, 3, 4].map(() => new Animated.Value(1))).current;

  const handleSubmit = async () => {
    if (rating < 1 || rating > 5) {
      Alert.alert(t('review.validationError'), t('review.ratingBetween'));
      return;
    }

    const rawComment = comment;
    const trimmedComment = rawComment.trim();
    if (rawComment.length > 0 && trimmedComment.length === 0) {
      Alert.alert(t('review.validationError'), t('review.commentMin'));
      return;
    }
    if (trimmedComment.length > 0 && trimmedComment.length < 10) {
      Alert.alert(t('review.validationError'), t('review.commentMin'));
      return;
    }

    try {
      setSubmitting(true);
      const deviceId = await deviceService.getDeviceId();
      const result = await reviewService.create({
        productId,
        comment: rawComment,
        rating,
        reviewerName: reviewerName.trim() || undefined,
        deviceId,
      });

      Alert.alert(t('common.success'), t('review.reviewSubmitted'), [
        {
          text: t('common.ok'),
          onPress: () => {
            navigation.goBack();
          },
        },
      ]);
    } catch (error) {
      console.error('Error submitting review:', error);
      Alert.alert(t('common.error'), t('review.failedToSubmit'));
    } finally {
      setSubmitting(false);
    }
  };

  const renderStarRating = () => {
    return (
      <View style={styles.starContainer}>
        {[1, 2, 3, 4, 5].map((star) => (
          <TouchableOpacity
            key={star}
            onPress={() => {
              setRating(star);
              const idx = star - 1;
              Animated.sequence([
                Animated.spring(starScales[idx], {
                  toValue: 1.18,
                  useNativeDriver: true,
                  speed: 22,
                  bounciness: 10,
                }),
                Animated.spring(starScales[idx], {
                  toValue: 1,
                  useNativeDriver: true,
                  speed: 18,
                  bounciness: 8,
                }),
              ]).start();
            }}
            style={styles.starButton}
          >
            <Animated.Text style={[styles.star, { transform: [{ scale: starScales[star - 1] }] }]}>
              {star <= rating ? '⭐' : '☆'}
            </Animated.Text>
          </TouchableOpacity>
        ))}
        <Text style={[styles.ratingText, { color: theme.colors.text }]}>{rating} / 5</Text>
      </View>
    );
  };

  return (
    <KeyboardAvoidingView
      style={[styles.container, { backgroundColor: theme.colors.background }]}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <OfflineBanner theme={theme} />
      <ScrollView contentContainerStyle={styles.scrollContent} keyboardShouldPersistTaps="handled">
        <View style={[styles.form, { backgroundColor: theme.colors.surface, borderColor: theme.colors.border }]}> 
          <Text style={[styles.label, { color: theme.colors.text }]}>{t('review.yourNameOptional')}</Text>
          <TextInput
            style={[styles.input, { color: theme.colors.text, borderColor: theme.colors.border, backgroundColor: theme.colors.surfaceAlt }]}
            value={reviewerName}
            onChangeText={setReviewerName}
            placeholder={t('review.enterYourName')}
            placeholderTextColor={theme.colors.textSecondary}
          />

          <Text style={[styles.label, { color: theme.colors.text }]}>{t('review.rating')}</Text>
          {renderStarRating()}

          <Text style={[styles.label, { color: theme.colors.text }]}>{t('review.reviewCommentOptional')}</Text>
          <TextInput
            style={[styles.input, styles.textArea, { color: theme.colors.text, borderColor: theme.colors.border, backgroundColor: theme.colors.surfaceAlt }]}
            value={comment}
            onChangeText={setComment}
            placeholder={t('review.writeYourReviewOptional')}
            placeholderTextColor={theme.colors.textSecondary}
            multiline
            numberOfLines={6}
            textAlignVertical="top"
          />
          <Text style={[styles.charCount, { color: theme.colors.textSecondary }]}>
            {comment.length} / 2000 {t('review.characters')}
          </Text>

          <TouchableOpacity
            style={[styles.submitButton, { backgroundColor: theme.colors.primary }, submitting && styles.submitButtonDisabled]}
            onPress={handleSubmit}
            disabled={submitting}
          >
            {submitting ? (
              <ActivityIndicator color="#fff" />
            ) : (
              <Text style={styles.submitButtonText}>{t('review.submitReview')}</Text>
            )}
          </TouchableOpacity>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  scrollContent: {
    padding: 20,
  },
  form: {
    borderRadius: 12,
    padding: 20,
    borderWidth: 1,
  },
  label: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 8,
    marginTop: 16,
  },
  input: {
    borderWidth: 1,
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
  },
  textArea: {
    minHeight: 120,
    textAlignVertical: 'top',
  },
  charCount: {
    fontSize: 12,
    marginTop: 4,
    textAlign: 'right',
  },
  starContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  starButton: {
    padding: 8,
  },
  star: {
    fontSize: 32,
  },
  ratingText: {
    fontSize: 18,
    fontWeight: 'bold',
    marginLeft: 16,
  },
  submitButton: {
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 24,
  },
  submitButtonDisabled: {
    backgroundColor: '#999',
  },
  submitButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default AddReviewScreen;


