import { ButtonGroup, Button } from '@blueprintjs/core';
import { push } from 'connected-react-router';
import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter, RouteComponentProps } from 'react-router';

import './SubmissionUserFilter.css';

interface SubmissionUserFilterProps extends RouteComponentProps {
  push: (url: string) => void;
}

class SubmissionUserFilter extends React.Component<SubmissionUserFilterProps> {
  render() {
    return (
      <ButtonGroup className="submission-user-filter" fill>
        <Button active={!this.isMine()} text="All submissions" onClick={this.clickAll} />
        <Button active={this.isMine()} text="My submissions" onClick={this.clickMine} />
      </ButtonGroup>
    );
  }

  private isMine = () => {
    return (this.props.location.pathname + '/').includes('/mine/');
  };

  private clickAll = () => {
    if (this.isMine()) {
      const idx = this.props.location.pathname.lastIndexOf('/mine');
      this.props.push(this.props.location.pathname.substr(0, idx));
    }
  };

  private clickMine = () => {
    if (!this.isMine()) {
      this.props.push((this.props.location.pathname + '/mine').replace('//', '/'));
    }
  };
}

const mapDispatchToProps = {
  push,
};

export default withRouter(connect(undefined, mapDispatchToProps)(SubmissionUserFilter));
