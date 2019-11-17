import * as React from 'react';

import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { ContentCardLink } from '../../../../components/ContentCardLink/ContentCardLink';
import { ProblemSet } from '../../../../modules/api/jerahmeel/problemSet';

import './ProblemSetCard.css';

export interface ProblemSetCardProps {
  problemSet: ProblemSet;
}

export class ProblemSetCard extends React.PureComponent<ProblemSetCardProps> {
  render() {
    const { problemSet } = this.props;

    return (
      <ContentCardLink to={`/problems/${problemSet.slug}`} className="problemset-card">
        <h4 className="problemset-card-name">{problemSet.name}</h4>
        {problemSet.description && (
          <>
            <hr />
            <HtmlText>{problemSet.description}</HtmlText>
          </>
        )}
      </ContentCardLink>
    );
  }
}
