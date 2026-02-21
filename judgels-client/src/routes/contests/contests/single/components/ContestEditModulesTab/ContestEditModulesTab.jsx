import { Intent } from '@blueprintjs/core';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { allModules } from '../../../../../../modules/api/uriel/contestModule';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import {
  contestModulesQueryOptions,
  disableContestModuleMutationOptions,
  enableContestModuleMutationOptions,
} from '../../../../../../modules/queries/contestModule';
import { ContestModuleCard } from '../ContestModuleCard/ContestModuleCard';

export default function ContestEditModulesTab() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const { data: modules } = useQuery(contestModulesQueryOptions(contest.jid));

  const enableModuleMutation = useMutation(enableContestModuleMutationOptions(contest.jid));
  const disableModuleMutation = useMutation(disableContestModuleMutationOptions(contest.jid));

  const renderContent = () => {
    if (!modules) {
      return <LoadingState />;
    }

    const enabledModules = allModules.filter(m => modules.indexOf(m) !== -1);
    const disabledModules = allModules.filter(m => modules.indexOf(m) === -1);

    return (
      <div className="contest-edit-dialog__content">
        <>{renderEnabledModules(enabledModules)}</>
        <hr />
        <>{renderDisabledModules(disabledModules)}</>
      </div>
    );
  };

  const renderEnabledModules = enabledModules => {
    if (enabledModules.length === 0) {
      return (
        <p>
          <small>No enabled modules.</small>
        </p>
      );
    }

    return enabledModules.map(module => (
      <ContestModuleCard
        key={module}
        type={module}
        intent={Intent.PRIMARY}
        buttonIntent={Intent.NONE}
        buttonText={'Disable'}
        buttonOnClick={type => disableModuleMutation.mutateAsync(type)}
        buttonIsLoading={disableModuleMutation.isPending}
        buttonIsDisabled={false}
      />
    ));
  };

  const renderDisabledModules = disabledModules => {
    if (disabledModules.length === 0) {
      return (
        <p>
          <small>No disabled modules.</small>
        </p>
      );
    }

    return disabledModules.map(module => (
      <ContestModuleCard
        key={module}
        type={module}
        intent={Intent.NONE}
        buttonIntent={Intent.PRIMARY}
        buttonText={'Enable'}
        buttonOnClick={type => enableModuleMutation.mutateAsync(type)}
        buttonIsLoading={enableModuleMutation.isPending}
        buttonIsDisabled={false}
      />
    ));
  };

  return (
    <>
      <h4>Modules settings</h4>
      <hr />
      {renderContent()}
    </>
  );
}
