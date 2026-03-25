import { Flex } from '@blueprintjs/labs';

import { sortLanguagesByName } from '../../modules/api/sandalphon/language';
import { useWebPrefs } from '../../modules/webPrefs';
import LanguageForm from './LanguageForm/LanguageForm';

import './LanguageWidget.scss';

export default function EditorialLanguageWidget({ defaultLanguage, editorialLanguages }) {
  const { editorialLanguage, setEditorialLanguage } = useWebPrefs();

  let initialLanguage;
  if (editorialLanguages.indexOf(editorialLanguage) !== -1) {
    initialLanguage = editorialLanguage;
  } else {
    initialLanguage = defaultLanguage;
  }
  const formProps = {
    form: 'editorial-language-form',
    languages: sortLanguagesByName(editorialLanguages),
    initialValues: {
      language: initialLanguage,
    },
  };

  return (
    <Flex className="language-widget" justifyContent="end">
      <div className="language-widget__right">
        <LanguageForm onSubmit={data => setEditorialLanguage(data.language)} {...formProps} />
      </div>
    </Flex>
  );
}
