import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { allLanguagesAllowed } from '../../../../../../modules/api/gabriel/language';
import { callAction } from '../../../../../../modules/callAction';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { useSession } from '../../../../../../modules/session';
import { formatDuration, parseDuration } from '../../../../../../utils/duration';
import ContestEditConfigsForm from '../ContestEditConfigsForm/ContestEditConfigsForm';
import { ContestEditConfigsTable } from '../ContestEditConfigsTable/ContestEditConfigsTable';

import * as contestModuleActions from '../../modules/contestModuleActions';

export default function ContestEditConfigsTab() {
  const { contestSlug } = useParams({ strict: false });
  const { token } = useSession();
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));

  const [state, setState] = useState({
    config: undefined,
    isEditing: false,
  });

  const refreshConfig = async () => {
    const config = await callAction(contestModuleActions.getConfig(contest.jid));
    setState(prevState => ({ ...prevState, config }));
  };

  useEffect(() => {
    refreshConfig();
  }, []);

  const render = () => {
    return (
      <>
        <h4>
          Configs settings
          {renderEditButton()}
        </h4>
        <hr />
        {renderContent()}
      </>
    );
  };

  const renderEditButton = () => {
    return (
      !state.isEditing && (
        <Button small className="right-action-button" intent={Intent.PRIMARY} icon={<Edit />} onClick={toggleEdit}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    const { config, isEditing } = state;
    if (config === undefined) {
      return <LoadingState />;
    }
    if (isEditing) {
      const {
        trocStyle,
        icpcStyle,
        ioiStyle,
        gcjStyle,
        scoreboard,
        clarificationTimeLimit,
        division,
        editorial,
        frozenScoreboard,
        mergedScoreboard,
        externalScoreboard,
        virtual,
      } = config;

      let initialValues = {
        scoreboardIsIncognito: scoreboard.isIncognitoScoreboard,
      };
      if (trocStyle) {
        initialValues = {
          ...initialValues,
          trocAllowAllLanguages: allLanguagesAllowed(trocStyle.languageRestriction),
          trocAllowedLanguages: fromLanguageRestriction(trocStyle.languageRestriction),
          trocWrongSubmissionPenalty: '' + trocStyle.wrongSubmissionPenalty,
        };
      }
      if (icpcStyle) {
        initialValues = {
          ...initialValues,
          icpcAllowAllLanguages: allLanguagesAllowed(icpcStyle.languageRestriction),
          icpcAllowedLanguages: fromLanguageRestriction(icpcStyle.languageRestriction),
          icpcWrongSubmissionPenalty: '' + icpcStyle.wrongSubmissionPenalty,
        };
      }
      if (ioiStyle) {
        initialValues = {
          ...initialValues,
          ioiAllowAllLanguages: allLanguagesAllowed(ioiStyle.languageRestriction),
          ioiAllowedLanguages: fromLanguageRestriction(ioiStyle.languageRestriction),
          ioiUsingLastAffectingPenalty: ioiStyle.usingLastAffectingPenalty,
          ioiUsingMaxScorePerSubtask: ioiStyle.usingMaxScorePerSubtask,
        };
      }
      if (gcjStyle) {
        initialValues = {
          ...initialValues,
          gcjAllowAllLanguages: allLanguagesAllowed(gcjStyle.languageRestriction),
          gcjAllowedLanguages: fromLanguageRestriction(gcjStyle.languageRestriction),
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
      if (editorial) {
        initialValues = {
          ...initialValues,
          editorialPreface: editorial.preface,
        };
      }
      if (frozenScoreboard) {
        initialValues = {
          ...initialValues,
          frozenScoreboardFreezeTime: formatDuration(frozenScoreboard.scoreboardFreezeTime),
          frozenScoreboardIsOfficialAllowed: frozenScoreboard.isOfficialScoreboardAllowed,
        };
      }
      if (mergedScoreboard) {
        initialValues = {
          ...initialValues,
          mergedScoreboardPreviousContestJid: mergedScoreboard.previousContestJid,
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
        onCancel: toggleEdit,
      };
      return <ContestEditConfigsForm initialValues={initialValues} onSubmit={upsertConfig} {...formProps} />;
    }
    return <ContestEditConfigsTable config={config} />;
  };

  const upsertConfig = async data => {
    const {
      trocStyle,
      icpcStyle,
      ioiStyle,
      gcjStyle,
      clarificationTimeLimit,
      division,
      editorial,
      frozenScoreboard,
      mergedScoreboard,
      externalScoreboard,
      virtual,
    } = state.config;

    let config = {
      scoreboard: {
        isIncognitoScoreboard: data.scoreboardIsIncognito,
      },
    };
    if (trocStyle) {
      const allowedLanguageNames = data.trocAllowAllLanguages ? [] : toLanguageRestriction(data.trocAllowedLanguages);
      config = {
        ...config,
        trocStyle: {
          languageRestriction: { allowedLanguageNames },
          wrongSubmissionPenalty: +data.trocWrongSubmissionPenalty,
        },
      };
    }
    if (icpcStyle) {
      const allowedLanguageNames = data.icpcAllowAllLanguages ? [] : toLanguageRestriction(data.icpcAllowedLanguages);
      config = {
        ...config,
        icpcStyle: {
          languageRestriction: { allowedLanguageNames },
          wrongSubmissionPenalty: +data.icpcWrongSubmissionPenalty,
        },
      };
    }
    if (ioiStyle) {
      const allowedLanguageNames = data.ioiAllowAllLanguages ? [] : toLanguageRestriction(data.ioiAllowedLanguages);
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
      const allowedLanguageNames = data.gcjAllowAllLanguages ? [] : toLanguageRestriction(data.gcjAllowedLanguages);
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
    if (editorial) {
      config = {
        ...config,
        editorial: { preface: data.editorialPreface },
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
    if (mergedScoreboard) {
      config = {
        ...config,
        mergedScoreboard: {
          previousContestJid: data.mergedScoreboardPreviousContestJid,
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

    await callAction(contestModuleActions.upsertConfig(contest.jid, config));
    await refreshConfig();
    toggleEdit();
  };

  const fromLanguageRestriction = r => {
    return Object.assign({}, ...r.allowedLanguageNames.map(l => ({ [l]: true })));
  };

  const toLanguageRestriction = r => {
    return Object.keys(r).filter(l => r[l]);
  };

  const toggleEdit = () => {
    setState(prevState => ({ ...prevState, isEditing: !prevState.isEditing }));
  };

  return render();
}
