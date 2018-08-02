import * as React from 'react';
import { connect } from 'react-redux';
import { change } from 'redux-form';

import { AppState } from 'modules/store';
import { selectStatementLanguage } from 'modules/webPrefs/webPrefsSelectors';
import { sortLanguagesByName } from 'modules/api/sandalphon/language';
import { webPrefsActions as injectedWebPrefsActions } from 'modules/webPrefs/webPrefsActions';

import StatementLanguageForm, { StatementLanguageFormData } from './StatementLanguageForm/StatementLanguageForm';

import './StatementLanguageWidget.css';

export interface StatementLanguageWidgetProps {
  defaultLanguage: string;
  statementLanguages: string[];
}

export interface StatementLanguageWidgetConnectedProps {
  statementLanguage: string;
  onSetInitialLanguage: (language: string) => void;
  onChangeLanguage: (data: StatementLanguageFormData) => Promise<void>;
}

class StatementLanguageWidget extends React.Component<
  StatementLanguageWidgetProps & StatementLanguageWidgetConnectedProps
> {
  componentDidMount() {
    const { statementLanguage, statementLanguages, defaultLanguage } = this.props;

    if (statementLanguages.indexOf(statementLanguage) !== -1) {
      this.props.onSetInitialLanguage(statementLanguage);
    } else {
      this.props.onSetInitialLanguage(defaultLanguage);
    }
  }

  componentDidUpdate(prevProps: StatementLanguageWidgetConnectedProps) {
    if (this.props.statementLanguage !== prevProps.statementLanguage) {
      this.componentDidMount();
    }
  }

  render() {
    const props = {
      onSubmit: this.props.onChangeLanguage,
      statementLanguages: sortLanguagesByName(this.props.statementLanguages),
    };

    return (
      <div className="statement-language-widget">
        <div className="statement-language-widget__right">
          <StatementLanguageForm {...props} />
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

  const mapDispatchToProps = dispatch => ({
    onChangeLanguage: (data: StatementLanguageFormData) =>
      dispatch(webPrefsActions.switchStatementLanguage(data.statementLanguage)),
    onSetInitialLanguage: (language: string) => dispatch(change('statement-language', 'statementLanguage', language)),
  });

  return connect(mapStateToProps, mapDispatchToProps)(StatementLanguageWidget);
}

export default createStatementLanguageWidget(injectedWebPrefsActions);
