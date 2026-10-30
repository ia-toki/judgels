import { Flex } from '@blueprintjs/labs';

import { sortLanguagesByName } from '../../modules/api/language';
import { useWebPrefs } from '../../modules/webPrefs';
import LanguageSelect from './LanguageSelect/LanguageSelect';

import './LanguageWidget.scss';

export default function EditorialLanguageWidget({ defaultLanguage, editorialLanguages }) {
  const { editorialLanguage, setEditorialLanguage } = useWebPrefs();

  let language;
  if (editorialLanguages.indexOf(editorialLanguage) !== -1) {
    language = editorialLanguage;
  } else {
    language = defaultLanguage;
  }

  return (
    <Flex justifyContent="end">
      <LanguageSelect
        value={language}
        languages={sortLanguagesByName(editorialLanguages)}
        onChange={setEditorialLanguage}
      />
    </Flex>
  );
}
