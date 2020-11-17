import * as React from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { ArchiveCreateDialog } from '../ArchiveCreateDialog/ArchiveCreateDialog';
import { ArchiveEditDialog } from '../ArchiveEditDialog/ArchiveEditDialog';
import { ArchivesTable } from '../ArchivesTable/ArchivesTable';
import {
  ArchivesResponse,
  ArchiveCreateData,
  Archive,
  ArchiveUpdateData,
} from '../../../../modules/api/jerahmeel/archive';
import * as archiveActions from '../modules/archiveActions';

export interface ArchivesPageProps {
  onGetArchives: () => Promise<ArchivesResponse>;
  onCreateArchive: (data: ArchiveCreateData) => Promise<void>;
  onUpdateArchive: (archiveJid: string, data: ArchiveUpdateData) => Promise<void>;
}

interface ArchivesPageState {
  response?: ArchivesResponse;
  isEditDialogOpen: boolean;
  editedArchive?: Archive;
}

class ArchivesPage extends React.Component<ArchivesPageProps, ArchivesPageState> {
  state: ArchivesPageState = {
    isEditDialogOpen: false,
  };

  componentDidMount() {
    this.refreshArchives();
  }

  render() {
    return (
      <ContentCard>
        <h3>Archives</h3>
        <hr />
        {this.renderCreateDialog()}
        {this.renderEditDialog()}
        {this.renderArchives()}
      </ContentCard>
    );
  }

  private refreshArchives = async () => {
    const response = await this.props.onGetArchives();
    this.setState({ response });
  };

  private renderCreateDialog = () => {
    return <ArchiveCreateDialog onCreateArchive={this.createArchive} />;
  };

  private renderEditDialog = () => {
    const { isEditDialogOpen, editedArchive } = this.state;
    return (
      <ArchiveEditDialog
        isOpen={isEditDialogOpen}
        archive={editedArchive}
        onUpdateArchive={this.updateArchive}
        onCloseDialog={() => this.editArchive(undefined)}
      />
    );
  };

  private renderArchives = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: archives } = response;
    if (archives.length === 0) {
      return (
        <p>
          <small>No archives.</small>
        </p>
      );
    }

    return <ArchivesTable archives={archives} onEditArchive={this.editArchive} />;
  };

  private createArchive = async (data: ArchiveCreateData) => {
    await this.props.onCreateArchive(data);
    await this.refreshArchives();
  };

  private editArchive = async (archive?: Archive) => {
    this.setState({
      isEditDialogOpen: !!archive,
      editedArchive: archive,
    });
  };

  private updateArchive = async (archiveJid: string, data: ArchiveUpdateData) => {
    await this.props.onUpdateArchive(archiveJid, data);
    this.editArchive(undefined);
    await this.refreshArchives();
  };
}

const mapDispatchToProps = {
  onGetArchives: archiveActions.getArchives,
  onCreateArchive: archiveActions.createArchive,
  onUpdateArchive: archiveActions.updateArchive,
};
export default connect(undefined, mapDispatchToProps)(ArchivesPage);
