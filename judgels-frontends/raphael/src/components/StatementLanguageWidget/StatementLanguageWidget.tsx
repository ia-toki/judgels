import * as React from 'react';
import { connect } from 'react-redux';

import { AppState } from '../../modules/store';
import { selectStatementLanguage } from '../../modules/webPrefs/webPrefsSelectors';
import { sortLanguagesByName } from '../../modules/api/sandalphon/language';
import { webPrefsActions as injectedWebPrefsActions } from '../../modules/webPrefs/webPrefsActions';

import StatementLanguageForm, { StatementLanguageFormData } from './StatementLanguageForm/StatementLanguageForm';

import './StatementLanguageWidget.css';

export interface StatementLanguageWidgetProps {
  defaultLanguage: string;
  statementLanguages: string[];
}

export interface StatementLanguageWidgetConnectedProps {
  statementLanguage: string;
  onChangeLanguage: (data: StatementLanguageFormData) => Promise<void>;
}

class StatementLanguageWidget extends React.Component<
  StatementLanguageWidgetProps & StatementLanguageWidgetConnectedProps
> {
  render() {
    const { statementLanguage, statementLanguages, defaultLanguage } = this.props;
    let initialLanguage;
    if (statementLanguages.indexOf(statementLanguage) !== -1) {
      initialLanguage = statementLanguage;
    } else {
      initialLanguage = defaultLanguage;
    }
    const formProps = {
      statementLanguages: sortLanguagesByName(this.props.statementLanguages),
      initialValues: {
        statementLanguage: initialLanguage,
      },
    };

    return (
      <div className="statement-language-widget">
        <div className="statement-language-widget__right">
          <StatementLanguageForm onSubmit={this.props.onChangeLanguage} {...formProps} />
        </div>
        <div className="clearfix" />
      </div>
    );
  }
}

export function createStatementLanguageWidget(webPrefsActions) {
  const mapStateToProps = (state: AppState) => ({
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onChangeLanguage: (data: StatementLanguageFormData) =>
      webPrefsActions.switchStatementLanguage(data.statementLanguage),
  };

  return connect(mapStateToProps, mapDispatchToProps)(StatementLanguageWidget);
}

export default createStatementLanguageWidget(injectedWebPrefsActions);
