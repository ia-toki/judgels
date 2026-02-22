import { Button, Card, Classes, Dialog, Intent } from '@blueprintjs/core';
import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import EditorialLanguageWidget from '../../../../../../../components/LanguageWidget/EditorialLanguageWidget';
import { ProblemEditorial } from '../../../../../../../components/ProblemEditorial/ProblemEditorial';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemEditorialQueryOptions,
  problemSetProblemQueryOptions,
} from '../../../../../../../modules/queries/problemSet';
import { useWebPrefs } from '../../../../../../../modules/webPrefs';

import './ProblemEditorialDialog.scss';

export default function ProblemEditorialDialog({ settersMap, profilesMap }) {
  const { problemSetSlug, problemAlias } = useParams({ strict: false });
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(problemSet.jid, problemAlias));
  const { editorialLanguage } = useWebPrefs();

  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const { data: response } = useQuery({
    ...problemSetProblemEditorialQueryOptions(problemSet.jid, problemAlias, { language: editorialLanguage }),
    enabled: isDialogOpen,
  });

  const toggleDialog = () => {
    setIsDialogOpen(prev => !prev);
  };

  const renderButton = () => {
    return (
      <Button
        className="problem-editorial-dialog-button"
        intent={Intent.WARNING}
        small
        onClick={toggleDialog}
        disabled={isDialogOpen}
      >
        View editorial
      </Button>
    );
  };

  const renderDialog = () => {
    return (
      <Dialog className="problem-editorial-dialog" isOpen={isDialogOpen} onClose={toggleDialog} title="Editorial">
        <div className={Classes.DIALOG_BODY}>
          {renderEditorialLanguageWidget()}
          <Card className="problem-editorial-card">{renderEditorial()}</Card>
        </div>
      </Dialog>
    );
  };

  const renderEditorial = () => {
    if (!response) {
      return null;
    }
    const { editorial } = response;

    return (
      <ProblemEditorial
        title={`${problemSet.name} - Problem ${problem.alias}`}
        containerName={problemSet.name}
        settersMap={settersMap}
        profilesMap={profilesMap}
      >
        {editorial.text}
      </ProblemEditorial>
    );
  };

  const renderEditorialLanguageWidget = () => {
    if (!response) {
      return null;
    }
    const { defaultLanguage, languages } = response.editorial;
    if (!defaultLanguage || !languages) {
      return null;
    }
    const props = {
      defaultLanguage,
      editorialLanguages: languages,
    };
    return (
      <div className="language-widget-wrapper">
        <EditorialLanguageWidget {...props} />
      </div>
    );
  };

  return (
    <div>
      {renderButton()}
      {renderDialog()}
    </div>
  );
}
