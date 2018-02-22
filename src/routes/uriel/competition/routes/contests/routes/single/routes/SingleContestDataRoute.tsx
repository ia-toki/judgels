import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { contestActions as injectedContestActions } from '../../../../../modules/contestActions';

export interface SingleContestDataRouteProps extends RouteComponentProps<{ contestJid: string }> {
  onFetchContest: (contestJid: string) => void;
  onClearContest: () => any;
}

class SingleContestDataRoute extends React.Component<SingleContestDataRouteProps> {
  componentDidMount() {
    this.refresh(this.props.match.params.contestJid);
  }

  componentWillReceiveProps(nextProps: SingleContestDataRouteProps) {
    if (nextProps.match.params.contestJid !== this.props.match.params.contestJid) {
      this.refresh(nextProps.match.params.contestJid);
    }
  }

  componentWillUnmount() {
    this.props.onClearContest();
  }

  render() {
    return null;
  }

  private refresh = (contestJid: string) => {
    this.props.onFetchContest(contestJid);
  };
}

export function createSingleContestDataRoute(contestActions) {
  const mapDispatchToProps = {
    onFetchContest: contestActions.fetch,
    onClearContest: contestActions.clear,
  };

  return withRouter<any>(connect(undefined, mapDispatchToProps)(SingleContestDataRoute));
}

export default createSingleContestDataRoute(injectedContestActions);
