import { Button, Intent } from '@blueprintjs/core';
import { Component } from 'react';
import { connect } from 'react-redux';

import { allLanguagesAllowed } from '../../../../../../modules/api/gabriel/language.js';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { formatDuration, parseDuration } from '../../../../../../utils/duration';

import { ContestEditConfigsTable } from '../ContestEditConfigsTable/ContestEditConfigsTable';
import ContestEditConfigsForm from '../ContestEditConfigsForm/ContestEditConfigsForm';
import { selectContest } from '../../../modules/contestSelectors';
import * as contestModuleActions from '../../modules/contestModuleActions';

class ContestEditConfigsTab extends Component {
  state = {
    config: undefined,
    isEditing: false,
  };

  async componentDidMount() {
    await this.refreshConfig();
  }

  render() {
    return (
      <>
        <h4>
          Configs settings
          {this.renderEditButton()}
        </h4>
        <hr />
        {this.renderContent()}
      </>
    );
  }

  refreshConfig = async () => {
    const config = await this.props.onGetConfig(this.props.contest.jid);
    this.setState({ config });
  };

  renderEditButton = () => {
    return (
      !this.state.isEditing && (
        <Button small className="right-action-button" intent={Intent.PRIMARY} icon="edit" onClick={this.toggleEdit}>
          Edit
        </Button>
      )
    );
  };

  renderContent = () => {
    const { config, isEditing } = this.state;
    if (config === undefined) {
      return <LoadingState />;
    }
    if (isEditing) {
      const {
        icpcStyle,
        ioiStyle,
        gcjStyle,
        scoreboard,
        clarificationTimeLimit,
        division,
        frozenScoreboard,
        externalScoreboard,
        virtual,
      } = config;

      let initialValues = {
        scoreboardIsIncognito: scoreboard.isIncognitoScoreboard,
      };
      if (icpcStyle) {
        initialValues = {
          ...initialValues,
          icpcAllowAllLanguages: allLanguagesAllowed(icpcStyle.languageRestriction),
          icpcAllowedLanguages: this.fromLanguageRestriction(icpcStyle.languageRestriction),
          icpcWrongSubmissionPenalty: '' + icpcStyle.wrongSubmissionPenalty,
        };
      }
      if (ioiStyle) {
        initialValues = {
          ...initialValues,
          ioiAllowAllLanguages: allLanguagesAllowed(ioiStyle.languageRestriction),
          ioiAllowedLanguages: this.fromLanguageRestriction(ioiStyle.languageRestriction),
          ioiUsingLastAffectingPenalty: ioiStyle.usingLastAffectingPenalty,
          ioiUsingMaxScorePerSubtask: ioiStyle.usingMaxScorePerSubtask,
        };
      }
      if (gcjStyle) {
        initialValues = {
          ...initialValues,
          gcjAllowAllLanguages: allLanguagesAllowed(gcjStyle.languageRestriction),
          gcjAllowedLanguages: this.fromLanguageRestriction(gcjStyle.languageRestriction),
          gcjWrongSubmissionPenalty: '' + gcjStyle.wrongSubmissionPenalty,
        };
      }
      if (clarificationTimeLimit) {
        initialValues = {
          ...initialValues,
          clarificationTimeLimitDuration: formatDuration(clarificationTimeLimit.clarificationDuration),
        };
      }
      if (division) {
        initialValues = {
          ...initialValues,
          divisionDivision: division.division,
        };
      }
      if (frozenScoreboard) {
        initialValues = {
          ...initialValues,
          frozenScoreboardFreezeTime: formatDuration(frozenScoreboard.scoreboardFreezeTime),
          frozenScoreboardIsOfficialAllowed: frozenScoreboard.isOfficialScoreboardAllowed,
        };
      }
      if (externalScoreboard) {
        initialValues = {
          ...initialValues,
          externalScoreboardReceiverUrl: externalScoreboard.receiverUrl,
          externalScoreboardReceiverSecret: externalScoreboard.receiverSecret,
        };
      }
      if (virtual) {
        initialValues = { ...initialValues, virtualDuration: formatDuration(virtual.virtualDuration) };
      }

      const formProps = {
        config,
        onCancel: this.toggleEdit,
      };
      return <ContestEditConfigsForm initialValues={initialValues} onSubmit={this.upsertConfig} {...formProps} />;
    }
    return <ContestEditConfigsTable config={config} />;
  };

  upsertConfig = async data => {
    const {
      icpcStyle,
      ioiStyle,
      gcjStyle,
      clarificationTimeLimit,
      division,
      frozenScoreboard,
      externalScoreboard,
      virtual,
    } = this.state.config;

    let config = {
      scoreboard: {
        isIncognitoScoreboard: data.scoreboardIsIncognito,
      },
    };
    if (icpcStyle) {
      const allowedLanguageNames = data.icpcAllowAllLanguages ? [] : Object.keys(data.icpcAllowedLanguages);
      config = {
        ...config,
        icpcStyle: {
          languageRestriction: { allowedLanguageNames },
          wrongSubmissionPenalty: +data.icpcWrongSubmissionPenalty,
        },
      };
    }
    if (ioiStyle) {
      const allowedLanguageNames = data.ioiAllowAllLanguages ? [] : Object.keys(data.ioiAllowedLanguages);
      config = {
        ...config,
        ioiStyle: {
          languageRestriction: { allowedLanguageNames },
          usingLastAffectingPenalty: data.ioiUsingLastAffectingPenalty,
          usingMaxScorePerSubtask: data.ioiUsingMaxScorePerSubtask,
        },
      };
    }
    if (gcjStyle) {
      const allowedLanguageNames = data.gcjAllowAllLanguages ? [] : Object.keys(data.gcjAllowedLanguages);
      config = {
        ...config,
        gcjStyle: {
          languageRestriction: { allowedLanguageNames },
          wrongSubmissionPenalty: +data.gcjWrongSubmissionPenalty,
        },
      };
    }
    if (clarificationTimeLimit) {
      config = {
        ...config,
        clarificationTimeLimit: { clarificationDuration: parseDuration(data.clarificationTimeLimitDuration) },
      };
    }
    if (division) {
      config = {
        ...config,
        division: { division: +data.divisionDivision },
      };
    }
    if (frozenScoreboard) {
      config = {
        ...config,
        frozenScoreboard: {
          scoreboardFreezeTime: parseDuration(data.frozenScoreboardFreezeTime),
          isOfficialScoreboardAllowed: data.frozenScoreboardIsOfficialAllowed,
        },
      };
    }
    if (externalScoreboard) {
      config = {
        ...config,
        externalScoreboard: {
          receiverUrl: data.externalScoreboardReceiverUrl,
          receiverSecret: data.externalScoreboardReceiverSecret,
        },
      };
    }
    if (virtual) {
      config = { ...config, virtual: { virtualDuration: parseDuration(data.virtualDuration) } };
    }

    await this.props.onUpsertConfig(this.props.contest.jid, config);
    await this.refreshConfig();
    this.toggleEdit();
  };

  fromLanguageRestriction = r => {
    return Object.assign({}, ...r.allowedLanguageNames.map(l => ({ [l]: true })));
  };

  toggleEdit = () => {
    this.setState(prevState => ({
      isEditing: !prevState.isEditing,
    }));
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
});
const mapDispatchToProps = {
  onGetConfig: contestModuleActions.getConfig,
  onUpsertConfig: contestModuleActions.upsertConfig,
};
export default connect(mapStateToProps, mapDispatchToProps)(ContestEditConfigsTab);
