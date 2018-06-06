import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../../components/LoadingState/LoadingState';
import { ContestClarificationCard } from '../ContestClarificationCard/ContestClarificationCard';
import { Contest } from '../../../../../../../../../../modules/api/uriel/contest';
import {
  ContestClarification,
  ContestClarificationsResponse,
} from '../../../../../../../../../../modules/api/uriel/contestClarification';
import { AppState } from '../../../../../../../../../../modules/store';
import { selectContest } from '../../../../../modules/contestSelectors';
import { contestClarificationActions as injectedContestClarificationActions } from '../modules/contestClarificationActions';
import { selectStatementLanguage } from '../../../../../../../../../../modules/webPrefs/webPrefsSelectors';

export interface ContestClarificationsPageProps {
  contest: Contest;
  statementLanguage: string;
  onFetchMyClarifications: (contestJid: string, language: string) => Promise<ContestClarificationsResponse>;
}

interface ContestClarificationsPageState {
  clarifications?: ContestClarification[];
  problemAliasesMap?: { [problemJid: string]: string };
  problemNamesMap?: { [problemJid: string]: string };
}

class ContestClarificationsPage extends React.Component<
  ContestClarificationsPageProps,
  ContestClarificationsPageState
> {
  state: ContestClarificationsPageState = {};

  async componentDidMount() {
    const { data, problemAliasesMap, problemNamesMap } = await this.props.onFetchMyClarifications(
      this.props.contest.jid,
      this.props.statementLanguage
    );
    this.setState({ clarifications: data, problemAliasesMap, problemNamesMap });
  }

  render() {
    return (
      <ContentCard>
        <h3>Clarifications</h3>
        <hr />
        {this.renderClarifications()}
      </ContentCard>
    );
  }

  private renderClarifications = () => {
    const { clarifications, problemAliasesMap, problemNamesMap } = this.state;
    if (!clarifications) {
      return <LoadingState />;
    }

    if (clarifications.length === 0) {
      return (
        <p>
          <small>
            <em>No clarifications.</em>
          </small>
        </p>
      );
    }

    return (
      <>
        {clarifications.map(clarification => (
          <ContestClarificationCard
            key={clarification.jid}
            clarification={clarification}
            problemAlias={problemAliasesMap![clarification.topicJid]}
            problemName={problemNamesMap![clarification.topicJid]}
          />
        ))}
      </>
    );
  };
}

function createContestClarificationsPage(contestClarificationActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onFetchMyClarifications: contestClarificationActions.fetchMyList,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestClarificationsPage));
}

export default createContestClarificationsPage(injectedContestClarificationActions);
