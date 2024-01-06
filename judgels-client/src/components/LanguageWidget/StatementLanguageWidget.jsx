import classNames from 'classnames';
import { connect } from 'react-redux';

import { sortLanguagesByName } from '../../modules/api/sandalphon/language';
import { selectStatementLanguage } from '../../modules/webPrefs/webPrefsSelectors';
import LanguageForm from './LanguageForm/LanguageForm';

import * as webPrefsActions from '../../modules/webPrefs/webPrefsActions';

import './LanguageWidget.scss';

function StatementLanguageWidget({
  className,
  defaultLanguage,
  statementLanguages,
  statementLanguage,
  onChangeLanguage,
}) {
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
        <LanguageForm onSubmit={onChangeLanguage} {...formProps} />
      </div>
      <div className="clearfix" />
    </div>
  );
}

const mapStateToProps = state => ({
  statementLanguage: selectStatementLanguage(state),
});

const mapDispatchToProps = {
  onChangeLanguage: data => webPrefsActions.switchStatementLanguage(data.language),
};

export default connect(mapStateToProps, mapDispatchToProps)(StatementLanguageWidget);
