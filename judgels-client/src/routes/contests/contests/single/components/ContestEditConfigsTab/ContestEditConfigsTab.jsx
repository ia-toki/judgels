import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { allLanguagesAllowed } from '../../../../../../modules/api/gabriel/language';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import {
  contestModuleConfigQueryOptions,
  upsertContestModuleConfigMutationOptions,
} from '../../../../../../modules/queries/contestModule';
import { formatDuration, parseDuration } from '../../../../../../utils/duration';
import ContestEditConfigsForm from '../ContestEditConfigsForm/ContestEditConfigsForm';
import { ContestEditConfigsTable } from '../ContestEditConfigsTable/ContestEditConfigsTable';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export default function ContestEditConfigsTab() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const { data: config } = useQuery(contestModuleConfigQueryOptions(contest.jid));

  const upsertConfigMutation = useMutation(upsertContestModuleConfigMutationOptions(contest.jid));

  const [isEditing, setIsEditing] = useState(false);

  const renderEditButton = () => {
    return (
      !isEditing && (
        <Button
          small
          className="right-action-button"
          intent={Intent.PRIMARY}
          icon={<Edit />}
          onClick={() => setIsEditing(true)}
        >
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
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
        onCancel: () => setIsEditing(false),
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
    } = config;

    let newConfig = {
      scoreboard: {
        isIncognitoScoreboard: data.scoreboardIsIncognito,
      },
    };
    if (trocStyle) {
      const allowedLanguageNames = data.trocAllowAllLanguages ? [] : toLanguageRestriction(data.trocAllowedLanguages);
      newConfig = {
        ...newConfig,
        trocStyle: {
          languageRestriction: { allowedLanguageNames },
          wrongSubmissionPenalty: +data.trocWrongSubmissionPenalty,
        },
      };
    }
    if (icpcStyle) {
      const allowedLanguageNames = data.icpcAllowAllLanguages ? [] : toLanguageRestriction(data.icpcAllowedLanguages);
      newConfig = {
        ...newConfig,
        icpcStyle: {
          languageRestriction: { allowedLanguageNames },
          wrongSubmissionPenalty: +data.icpcWrongSubmissionPenalty,
        },
      };
    }
    if (ioiStyle) {
      const allowedLanguageNames = data.ioiAllowAllLanguages ? [] : toLanguageRestriction(data.ioiAllowedLanguages);
      newConfig = {
        ...newConfig,
        ioiStyle: {
          languageRestriction: { allowedLanguageNames },
          usingLastAffectingPenalty: data.ioiUsingLastAffectingPenalty,
          usingMaxScorePerSubtask: data.ioiUsingMaxScorePerSubtask,
        },
      };
    }
    if (gcjStyle) {
      const allowedLanguageNames = data.gcjAllowAllLanguages ? [] : toLanguageRestriction(data.gcjAllowedLanguages);
      newConfig = {
        ...newConfig,
        gcjStyle: {
          languageRestriction: { allowedLanguageNames },
          wrongSubmissionPenalty: +data.gcjWrongSubmissionPenalty,
        },
      };
    }
    if (clarificationTimeLimit) {
      newConfig = {
        ...newConfig,
        clarificationTimeLimit: { clarificationDuration: parseDuration(data.clarificationTimeLimitDuration) },
      };
    }
    if (division) {
      newConfig = {
        ...newConfig,
        division: { division: +data.divisionDivision },
      };
    }
    if (editorial) {
      newConfig = {
        ...newConfig,
        editorial: { preface: data.editorialPreface },
      };
    }
    if (frozenScoreboard) {
      newConfig = {
        ...newConfig,
        frozenScoreboard: {
          scoreboardFreezeTime: parseDuration(data.frozenScoreboardFreezeTime),
          isOfficialScoreboardAllowed: data.frozenScoreboardIsOfficialAllowed,
        },
      };
    }
    if (mergedScoreboard) {
      newConfig = {
        ...newConfig,
        mergedScoreboard: {
          previousContestJid: data.mergedScoreboardPreviousContestJid,
        },
      };
    }
    if (externalScoreboard) {
      newConfig = {
        ...newConfig,
        externalScoreboard: {
          receiverUrl: data.externalScoreboardReceiverUrl,
          receiverSecret: data.externalScoreboardReceiverSecret,
        },
      };
    }
    if (virtual) {
      newConfig = { ...newConfig, virtual: { virtualDuration: parseDuration(data.virtualDuration) } };
    }

    await upsertConfigMutation.mutateAsync(newConfig, {
      onSuccess: () => toastActions.showSuccessToast('Configs updated.'),
    });
    setIsEditing(false);
  };

  const fromLanguageRestriction = r => {
    return Object.assign({}, ...r.allowedLanguageNames.map(l => ({ [l]: true })));
  };

  const toLanguageRestriction = r => {
    return Object.keys(r).filter(l => r[l]);
  };

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
}
