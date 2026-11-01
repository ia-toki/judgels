import { describe, expect, test } from 'vitest';

import { splitCourseDescription } from './splitCourseDescription';

describe('splitCourseDescription', () => {
  test('takes the image out of an image-only first paragraph', () => {
    const description = '<p><img src="https://example.com/course.png" height="120"></p>\n<p>Learn loops</p>';

    expect(splitCourseDescription(description)).toEqual({
      imageUrl: 'https://example.com/course.png',
      text: '<p>Learn loops</p>',
    });
  });

  test('leaves no text when the description is only the image', () => {
    const description = '<p><img src="https://example.com/course.png"></p>';

    expect(splitCourseDescription(description)).toEqual({ imageUrl: 'https://example.com/course.png', text: '' });
  });

  test('keeps an image that comes after other content', () => {
    const description = '<p>Learn loops</p><p><img src="https://example.com/loop.png"></p>';

    expect(splitCourseDescription(description)).toEqual({ text: description });
  });

  test('keeps an image in a first paragraph that also has text', () => {
    const description = '<p>Learn <img src="https://example.com/loop.png"> loops</p>';

    expect(splitCourseDescription(description)).toEqual({ text: description });
  });

  test('keeps an image-only paragraph that follows bare text', () => {
    const description = 'Learn loops<p><img src="https://example.com/loop.png"></p>';

    expect(splitCourseDescription(description)).toEqual({ text: description });
  });

  test('handles a missing description', () => {
    expect(splitCourseDescription(undefined)).toEqual({ text: '' });
  });
});
