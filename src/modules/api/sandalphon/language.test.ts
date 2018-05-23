import { consolidateDefaultLanguages, sortLanguagesByName } from './language';

describe('language', () => {
  test('sortLanguagesByName()', () => {
    expect(sortLanguagesByName(['ca', 'en', 'hr', 'zh'])).toEqual(['ca', 'zh', 'hr', 'en']);
  });

  describe('consolidateDefaultLanguages()', () => {
    it('returns the current statement language when it is one of the default languages', () => {
      expect(consolidateDefaultLanguages(['id', 'en', 'id', 'hr'], 'id')).toEqual({
        defaultLanguage: 'id',
        uniqueDefaultLanguages: ['id', 'en', 'hr'],
      });
      expect(consolidateDefaultLanguages(['id', 'en', 'id', 'hr'], 'en')).toEqual({
        defaultLanguage: 'en',
        uniqueDefaultLanguages: ['id', 'en', 'hr'],
      });
    });

    it('returns the most common default languages when the current statement language is not one of the default languages', () => {
      expect(consolidateDefaultLanguages(['id', 'en', 'id', 'hr'], 'fr')).toEqual({
        defaultLanguage: 'id',
        uniqueDefaultLanguages: ['id', 'en', 'hr'],
      });
      expect(consolidateDefaultLanguages(['id', 'en', 'id', 'hr', 'hr'], 'ar')).toEqual({
        defaultLanguage: 'hr',
        uniqueDefaultLanguages: ['id', 'en', 'hr'],
      });
    });
  });
});
