import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from '../../../../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { ChapterProblemWorksheet } from '../../../../../../../../../modules/api/jerahmeel/chapterProblemBundle';
import { ProblemWorksheetCard } from '../../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';

import './ChapterProblemPage.css';

export interface ChapterProblemPageProps extends RouteComponentProps<{ problemAlias: string }> {
  worksheet: ChapterProblemWorksheet;
}

export class ChapterProblemPage extends React.Component<ChapterProblemPageProps> {
  render() {
    return (
      <ContentCard>
        {this.renderStatementLanguageWidget()}
        {this.renderStatement()}
      </ContentCard>
    );
  }

  private renderStatementLanguageWidget = () => {
    const { defaultLanguage, languages } = this.props.worksheet;
    if (!defaultLanguage || !languages) {
      return null;
    }
    const props: StatementLanguageWidgetProps = {
      defaultLanguage: defaultLanguage,
      statementLanguages: languages,
    };
    return (
      <div className="chapter-bundle-problem-page__widget">
        <StatementLanguageWidget {...props} />
      </div>
    );
  };

  private renderStatement = () => {
    const { problem, worksheet } = this.props.worksheet;
    if (!problem || !worksheet) {
      return <LoadingState />;
    }

    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        latestSubmissions={{}}
        onAnswerItem={this.onCreateSubmission}
        worksheet={worksheet}
      />
    );
  };

  private onCreateSubmission = async (itemJid: string, answer: string) => {
    return await null;
  };
}

export function createChapterProblemPage() {
  return withRouter<any, any>(connect()(ChapterProblemPage));
}

export default createChapterProblemPage();
