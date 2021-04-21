'use strict';

import * as path from 'path';
import * as os from 'os';

import {Trace} from 'vscode-jsonrpc';
import { window, workspace, ExtensionContext, StatusBarAlignment } from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions, StreamInfo } from 'vscode-languageclient';
import * as net from 'net';
import { stat } from 'fs'

const ServerName = 'Bahn IDE server'

const statusBarItem = window.createStatusBarItem(StatusBarAlignment.Right, 0)

export function activate(context: ExtensionContext) {
    
    
    // start LC
    let lc = createEmbeddedClient(context);
    lc.trace = Trace.Verbose;
    updateStatusBar(`$(loading) Starting ${ServerName}...`);
    lc.onReady().then(() => {
        updateStatusBar(`$(pass) ${ServerName}`);
    });
    // keep disposable for deactivating
    context.subscriptions.push(lc.start());
    context.subscriptions.push(statusBarItem);
}

function updateStatusBar(text: string) {
    statusBarItem.text = text;
    statusBarItem.show();
}

// create LanguageClient that works with embedded BahnDSL language server binary
function createEmbeddedClient(context: ExtensionContext): LanguageClient {
    let launcher = os.platform() === 'win32' ? 'bahn-ide-server.bat' : 'bahn-ide-server';
    let script = context.asAbsolutePath(path.join('bahn-ide-server', 'bin', launcher));
    // let debugEnv = Object.assign({
    //     JAVA_OPTS:"-Xdebug -Xrunjdwp:server=y,transport=dt_socket,suspend=n,quiet=y"
    // }, process.env)

    let serverOptions: ServerOptions = {
        run : { command: script },
        debug: { command: script, args: ['-log', '-trace'] }
    };

    return new LanguageClient(ServerName, serverOptions, createClientOptions());
}

// create LanguageClient that works with remote LSP (used for debugging)
function createRemoteClient(context: ExtensionContext): LanguageClient {
    
    let serverInfo = () => {
        let socket = net.connect({port: 8081});
        let result: StreamInfo = {
            writer: socket,
            reader: socket
        };
        return Promise.resolve(result);
    };
	
	return new LanguageClient(`Remote ${ServerName}`, serverInfo, createClientOptions());
}

function createClientOptions(): LanguageClientOptions {
    // return {
    //     documentSelector: ['bahn'],
    //     synchronize: {
    //         fileEvents: workspace.createFileSystemWatcher('**/*.bahn')
    //     }
    // };

    return { documentSelector: [{ scheme: 'file', language: 'bahn' }] }
}