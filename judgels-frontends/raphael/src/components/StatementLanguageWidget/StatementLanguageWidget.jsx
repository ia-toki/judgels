import * as React from 'react';
import { connect } from 'react-redux';

import { selectStatementLanguage } from '../../modules/webPrefs/webPrefsSelectors';
import { sortLanguagesByName } from '../../modules/api/sandalphon/language';
import StatementLanguageForm from './StatementLanguageForm/StatementLanguageForm';
import * as webPrefsActions from '../../modules/webPrefs/webPrefsActions';

import './StatementLanguageWidget.css';

function StatementLanguageWidget({ defaultLanguage, statementLanguages, statementLanguage, onChangeLanguage }) {
  let initialLanguage;
  if (statementLanguages.indexOf(statementLanguage) !== -1) {
    initialLanguage = statementLanguage;
  } else {
    initialLanguage = defaultLanguage;
  }
  const formProps = {
    statementLanguages: sortLanguagesByName(statementLanguages),
    initialValues: {
      statementLanguage: initialLanguage,
    },
  };

  return (
    <div className="statement-language-widget">
      <div className="statement-language-widget__right">
        <StatementLanguageForm onSubmit={onChangeLanguage} {...formProps} />
      </div>
      <div className="clearfix" />
    </div>
  );
}

const mapStateToProps = state => ({
  statementLanguage: selectStatementLanguage(state),
});

const mapDispatchToProps = {
  onChangeLanguage: data => webPrefsActions.switchStatementLanguage(data.statementLanguage),
};

export default connect(mapStateToProps, mapDispatchToProps)(StatementLanguageWidget);
