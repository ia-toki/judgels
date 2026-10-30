import { Flex } from '@blueprintjs/labs';

import { sortLanguagesByName } from '../../modules/api/language';
import { useWebPrefs } from '../../modules/webPrefs';
import LanguageSelect from './LanguageSelect/LanguageSelect';

import './LanguageWidget.scss';

export default function StatementLanguageWidget({ className, defaultLanguage, statementLanguages }) {
  const { statementLanguage, setStatementLanguage } = useWebPrefs();

  let language;
  if (statementLanguages.indexOf(statementLanguage) !== -1) {
    language = statementLanguage;
  } else {
    language = defaultLanguage;
  }

  return (
    <Flex className={className} justifyContent="end">
      <LanguageSelect
        value={language}
        languages={sortLanguagesByName(statementLanguages)}
        onChange={setStatementLanguage}
      />
    </Flex>
  );
}
