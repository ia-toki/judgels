import { Component } from 'react';
import { withRouter } from 'react-router';
import { Link } from 'react-router-dom';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import ProblemEditorialDialog from '../ProblemEditorialDialog/ProblemEditorialDialog';
import { selectProblemSet } from '../../../../modules/problemSetSelectors';
import * as problemSetProblemActions from '../../modules/problemSetProblemActions';

class ProblemMetadataWidget extends Component {
  state = {
    response: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetProblemMetadata(
      this.props.problemSet.jid,
      this.props.match.params.problemAlias
    );
    this.setState({ response });
  }

  render() {
    return (
      <>
        {this.renderContests()}
        {this.renderSpoilers()}
      </>
    );
  }

  renderContests = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }

    const { contests } = response;
    if (contests.length == 0) {
      return null;
    }

    return (
      <ContentCard>
        <h4>Contests</h4>
        <ul>
          {contests.map(c => (
            <li key={c.slug}>
              <Link to={`/contests/${c.slug}`}>{c.name}</Link>
            </li>
          ))}
        </ul>
      </ContentCard>
    );
  };

  renderSpoilers = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }

    const { metadata, profilesMap } = response;
    if (!metadata.hasEditorial) {
      return null;
    }

    return (
      <ContentCard>
        <h4>Spoilers</h4>
        <ProblemEditorialDialog settersMap={metadata.settersMap} profilesMap={profilesMap} />
      </ContentCard>
    );
  };
}

const mapStateToProps = state => ({
  problemSet: selectProblemSet(state),
});
const mapDispatchToProps = {
  onGetProblemMetadata: problemSetProblemActions.getProblemMetadata,
};
export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ProblemMetadataWidget));
