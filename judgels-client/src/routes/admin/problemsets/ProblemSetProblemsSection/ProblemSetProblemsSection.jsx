import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { Alias } from '../../../../components/forms/validations';
import { ProblemType } from '../../../../modules/api/sandalphon/problem';
import {
  problemSetProblemsQueryOptions,
  setProblemSetProblemsMutationOptions,
} from '../../../../modules/queries/problemSet';
import ProblemSetProblemsEditForm from '../ProblemSetProblemsEditForm/ProblemSetProblemsEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function ProblemSetProblemsSection({ problemSet }) {
  const { data: response } = useSuspenseQuery(problemSetProblemsQueryOptions(problemSet.jid));
  const setProblemsMutation = useMutation(setProblemSetProblemsMutationOptions(problemSet.jid));

  const [isEditing, setIsEditing] = useState(false);

  const updateProblems = data => {
    const problems = deserializeProblems(data.problems);
    setProblemsMutation.mutate(problems, {
      onSuccess: () => toastActions.showSuccessToast('Problemset problems updated.'),
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
      const initialValues = {
        problems: serializeProblems(response.data, response.problemsMap, response.contestsMap),
      };
      return (
        <ProblemSetProblemsEditForm
          initialValues={initialValues}
          validator={validateProblems}
          onSubmit={updateProblems}
          onCancel={() => setIsEditing(false)}
        />
      );
    }
    const { data, problemsMap, contestsMap } = response;
    const rows = data.map(problem => (
      <tr key={problem.problemJid}>
        <td>{problem.alias}</td>
        <td>{problemsMap[problem.problemJid] && problemsMap[problem.problemJid].slug}</td>
        <td>
          {problem.contestJids
            .map(jid => contestsMap[jid])
            .filter(c => c)
            .map(c => c.slug)
            .join(';')}
        </td>
      </tr>
    ));
    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th style={{ width: '50px' }}>Alias</th>
            <th>Slug</th>
            <th>Contest</th>
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

function serializeProblems(problems, problemsMap, contestsMap) {
  return problems
    .map(p => {
      if (p.contestJids.length > 0) {
        const contestSlugs = p.contestJids
          .map(jid => contestsMap[jid])
          .filter(c => c)
          .map(c => c.slug)
          .join(';');
        return `${p.alias},${problemsMap[p.problemJid].slug},${p.type},${contestSlugs}`;
      } else if (p.type !== ProblemType.Programming) {
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
      contestSlugs: (s[3] || '')
        .split(';')
        .filter(slug => slug)
        .map(slug => slug.trim()),
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
    if (p.length < 2 || p.length > 4) {
      return 'Each line must contain 2-4 comma-separated elements';
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
