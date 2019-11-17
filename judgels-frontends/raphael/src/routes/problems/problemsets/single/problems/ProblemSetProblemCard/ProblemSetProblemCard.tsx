import * as React from 'react';

import { ContentCardLink } from '../../../../../../components/ContentCardLink/ContentCardLink';
import { ProblemSet } from '../../../../../../modules/api/jerahmeel/problemSet';
import { ProblemSetProblem } from '../../../../../../modules/api/jerahmeel/problemSetProblem';

export interface ProblemSetProblemCardProps {
  problemSet: ProblemSet;
  problem: ProblemSetProblem;
  problemName: string;
}

export class ProblemSetProblemCard extends React.PureComponent<ProblemSetProblemCardProps> {
  render() {
    const { problemSet, problem, problemName } = this.props;

    return (
      <ContentCardLink to={`/problems/${problemSet.slug}/${problem.alias}`}>
        <span data-key="name">
          {problem.alias}. {problemName}
        </span>
      </ContentCardLink>
    );
  }
}
