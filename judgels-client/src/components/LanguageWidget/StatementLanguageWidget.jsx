import classNames from 'classnames';

import { sortLanguagesByName } from '../../modules/api/sandalphon/language';
import { useWebPrefs } from '../../modules/webPrefs';
import LanguageForm from './LanguageForm/LanguageForm';

import './LanguageWidget.scss';

export default function StatementLanguageWidget({ className, defaultLanguage, statementLanguages }) {
  const { statementLanguage, setStatementLanguage } = useWebPrefs();

  let initialLanguage;
  if (statementLanguages.indexOf(statementLanguage) !== -1) {
    initialLanguage = statementLanguage;
  } else {
    initialLanguage = defaultLanguage;
  }
  const formProps = {
    form: 'statement-language-form',
    languages: sortLanguagesByName(statementLanguages),
    initialValues: {
      language: initialLanguage,
    },
  };

  return (
    <div className={classNames('language-widget', className)}>
      <div className="language-widget__right">
        <LanguageForm onSubmit={data => setStatementLanguage(data.language)} {...formProps} />
      </div>
      <div className="clearfix" />
    </div>
  );
}
