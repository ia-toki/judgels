import { consolidateLanguages, sortLanguagesByName } from './language';
import { ProblemInfo } from './problem';

describe('language', () => {
  test('sortLanguagesByName()', () => {
    expect(sortLanguagesByName(['ca', 'en', 'hr', 'zh'])).toEqual(['ca', 'zh', 'hr', 'en']);
  });

  describe('consolidateLanguages()', () => {
    const mk = (defaultLanguage, languages) =>
      ({
        defaultLanguage,
        titlesByLanguage: Object.assign({}, ...languages.map(lang => ({ [lang]: 'name' }))),
      } as ProblemInfo);

    let problemsMap;
    it('returns the current statement language when it is one of the languages', () => {
      problemsMap = Object.assign({}, [mk('id', ['id', 'en']), mk('id', ['id', 'hr'])]);
      expect(consolidateLanguages(problemsMap, 'id')).toEqual({
        defaultLanguage: 'id',
        uniqueLanguages: ['id', 'en', 'hr'],
      });
      expect(consolidateLanguages(problemsMap, 'en')).toEqual({
        defaultLanguage: 'en',
        uniqueLanguages: ['id', 'en', 'hr'],
      });
    });

    it('returns the most common default languages when the current statement language is not one of the languages', () => {
      problemsMap = Object.assign({}, [
        mk('id', ['id', 'en']),
        mk('id', ['id', 'hr']),
        mk('en', ['en']),
        mk('hr', ['hr']),
      ]);
      expect(consolidateLanguages(problemsMap, 'fr')).toEqual({
        defaultLanguage: 'id',
        uniqueLanguages: ['id', 'en', 'hr'],
      });
      problemsMap = Object.assign({}, [
        mk('id', ['id', 'en']),
        mk('id', ['id', 'hr']),
        mk('en', ['en']),
        mk('hr', ['hr']),
        mk('hr', ['hr']),
      ]);
      expect(consolidateLanguages(problemsMap, 'ar')).toEqual({
        defaultLanguage: 'hr',
        uniqueLanguages: ['id', 'en', 'hr'],
      });
    });
  });
});
