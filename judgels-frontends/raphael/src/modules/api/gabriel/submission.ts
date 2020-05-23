export const DEFAULT_SOURCE_KEY = 'source';

export interface SourceFile {
  name: string;
  content: string;
}

export interface SubmissionSource {
  submissionFiles: { [key: string]: SourceFile };
}
