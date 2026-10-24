import { OptionSelect } from '../../../../components/OptionSelect/OptionSelect';
import { worldLanguageNamesMap } from '../../../../modules/api/worldLanguage';

const getLanguageName = language => worldLanguageNamesMap[language] || language;

export default function LanguageSelect({ languages, label = 'Language', ...props }) {
  const sortedLanguages = languages.slice().sort((a, b) => getLanguageName(a).localeCompare(getLanguageName(b)));

  return (
    <OptionSelect
      options={sortedLanguages}
      getOptionName={getLanguageName}
      label={label}
      filterLabel="Filter languages"
      noResultsText="No languages found."
      {...props}
    />
  );
}
