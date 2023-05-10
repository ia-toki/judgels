import { Intent, Button } from '@blueprintjs/core';
import { Component } from 'react';
import { connect } from 'react-redux';

import * as userAccountActions from '../../modules/userAccountActions';

class ResendActivationEmailButton extends Component {
  timer;

  state = {
    timeRemainingResendEmail: 5,
  };

  componentDidMount() {
    this.timer = setInterval(this.countDown, 1000);
  }

  componentWillUnmount() {
    if (this.timer) {
      clearTimeout(this.timer);
      this.timer = 0;
    }
  }

  render() {
    const { timeRemainingResendEmail } = this.state;
    return (
      <Button
        type="submit"
        text={`Resend activation email${timeRemainingResendEmail > 0 ? ` (${timeRemainingResendEmail})` : ''}`}
        intent={Intent.PRIMARY}
        onClick={this.onResendEmail}
        disabled={timeRemainingResendEmail > 0}
      />
    );
  }

  onResendEmail = async () => {
    await this.props.onResendActivationEmail(this.props.email);
    this.setState({ timeRemainingResendEmail: 5 });
    this.timer = setInterval(this.countDown, 1000);
  };

  countDown = () => {
    const seconds = this.state.timeRemainingResendEmail - 1;
    this.setState({ timeRemainingResendEmail: seconds });

    if (seconds === 0 && this.timer) {
      clearInterval(this.timer);
    }
  };
}

const mapDispatchToProps = {
  onResendActivationEmail: userAccountActions.resendActivationEmail,
};

export default connect(undefined, mapDispatchToProps)(ResendActivationEmailButton);
