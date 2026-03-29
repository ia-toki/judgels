import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { Alias } from '../../../../components/forms/validations';
import { ProblemType } from '../../../../modules/api/sandalphon/problem';
import {
  chapterProblemsQueryOptions,
  setChapterProblemsMutationOptions,
} from '../../../../modules/queries/chapterProblem';
import ChapterProblemsEditForm from '../ChapterProblemsEditForm/ChapterProblemsEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function ChapterProblemsSection({ chapter }) {
  const { data: response } = useSuspenseQuery(chapterProblemsQueryOptions(chapter.jid));
  const setProblemsMutation = useMutation(setChapterProblemsMutationOptions(chapter.jid));

  const [isEditing, setIsEditing] = useState(false);

  const updateProblems = data => {
    const problems = deserializeProblems(data.problems);
    setProblemsMutation.mutate(problems, {
      onSuccess: () => toastActions.showSuccessToast('Chapter problems updated.'),
    });
    setIsEditing(false);
  };

  const renderEditButton = () => {
    return (
      !isEditing && (
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = { problems: serializeProblems(response.data, response.problemsMap) };
      return (
        <ChapterProblemsEditForm
          initialValues={initialValues}
          validator={validateProblems}
          onSubmit={updateProblems}
          onCancel={() => setIsEditing(false)}
        />
      );
    }
    const { data, problemsMap } = response;
    const rows = data.map(problem => (
      <tr key={problem.problemJid}>
        <td>{problem.alias}</td>
        <td>{problemsMap[problem.problemJid] && problemsMap[problem.problemJid].slug}</td>
        <td>{problem.type}</td>
      </tr>
    ));
    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th style={{ width: '50px' }}>Alias</th>
            <th>Slug</th>
            <th style={{ width: '150px' }}>Type</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  return (
    <div>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>Problems</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}

function serializeProblems(problems, problemsMap) {
  return problems
    .map(p => {
      if (p.type !== ProblemType.Programming) {
        return `${p.alias},${problemsMap[p.problemJid].slug},${p.type}`;
      } else {
        return `${p.alias},${problemsMap[p.problemJid].slug}`;
      }
    })
    .join('\n');
}

function deserializeProblems(problems) {
  return problems
    .split('\n')
    .map(s => s.trim())
    .filter(s => s.length > 0)
    .map(s => s.split(','))
    .map(s => s.map(t => t.trim()))
    .map(s => ({
      alias: s[0],
      slug: s[1],
      type: s[2] || ProblemType.Programming,
    }));
}

function validateProblems(value) {
  const problems = value
    .split('\n')
    .map(s => s.trim())
    .filter(s => s.length > 0)
    .map(s => s.split(','))
    .map(s => s.map(t => t.trim()));

  const aliases = [];
  const slugs = [];

  for (const p of problems) {
    if (p.length < 2 || p.length > 3) {
      return 'Each line must contain 2-3 comma-separated elements';
    }
    const alias = p[0];
    const aliasValidation = Alias(alias);
    if (aliasValidation) {
      return 'Problem aliases: ' + aliasValidation;
    }

    const slug = p[1];

    aliases.push(alias);
    slugs.push(slug);
  }

  if (new Set(aliases).size !== aliases.length) {
    return 'Problem aliases must be unique';
  }
  if (new Set(slugs).size !== slugs.length) {
    return 'Problem slugs must be unique';
  }

  return undefined;
}
