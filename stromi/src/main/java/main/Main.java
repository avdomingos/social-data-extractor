/*
*	This file is part of STRoMI.
*
*    STRoMI is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    STRoMI is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with STRoMI.  If not, see <http://www.gnu.org/licenses/>.
*/

package main;


import org.apache.log4j.PropertyConfigurator;
import pt.command.base.CommandResolver;
import pt.command.commands.*;
import pt.command.exception.UnsupportedCommandException;
import pt.command.parser.CommandLineParser;
import pt.exception.*;
import pt.fabric.DistributedMonitorization;
import pt.fabric.Monitorization;
import pt.intance.provider.*;
import pt.intance.provider.hash.MongoHashConnectionInstanceProvider;
import pt.intance.provider.hash.UsersHashPersisProviderInstanceProvider;
import pt.intance.provider.hash.UsersWorkerHashCollectionInstanceProvider;
import pt.model.TypeOfInstance;
import pt.properties.PropertyFileLoader;
import pt.utils.LogUtils;
import pt.utils.TwitterUsersProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class Main {


    public static void main(String[] args) throws NotInitializedException, InvalidPropertiesException, IOException, InstanceInitializeException {
        try {
            init(TypeOfInstance.DISTRIBUTED);
            setUpCommands();
            CommandLineParser clp =
                    new CommandLineParser(
                            CommandLineParser.DEFAULT_ARGUMENT_PREFIX,
                            CommandLineParser.DEFAULT_ARGUMENT_VALUE_SEPARATOR);

            InputStreamReader inputStream = new InputStreamReader(System.in);
            BufferedReader consoleInput = new BufferedReader(inputStream);

            String command;
            LogUtils.logInfo("Main started");
            System.out.print(">");
            while (!(command = consoleInput.readLine()).trim().equalsIgnoreCase("exit")) {
                try {
                    if (command != null && !command.isEmpty()) {
                        CommandResolver.execute(System.out, command);
                    }
                } catch (UnsupportedCommandException e) {
                    System.err.println("Unsupported command. To get more help please use write help");
                }
                System.out.print(">");
            }
            finalizer();
        } catch (RuntimeException e) {
            System.out.print("RUNTIME EXCEPTION");
            Monitorization.getInstance().forceToClose();
            e.printStackTrace();
        }
    }


    private static void init(TypeOfInstance typeOfInstance) throws InstanceInitializeException {
        PropertyConfigurator.configure("log4j.properties");
        NetworkInterfaceInstanceProvider.init();
        MongoConnectionInstanceProvider.init();

        if (typeOfInstance.equals(TypeOfInstance.NORMAL)) {
            initNormal();
        } else if (typeOfInstance.equals(TypeOfInstance.DISTRIBUTED)) {
            initDistributed();
        }

        try {
            TwitterUsersProvider.getInstance().refreshList();
        } catch (IOException e) {
            throw new InstanceInitializeException(e.getMessage());
        } catch (InvalidPropertiesException e) {
            throw new InstanceInitializeException(e.getMessage());
        }

    }

    private static void initNormal() throws InstanceInitializeException {
        UsersWorkerInstanceProvider.init();
        try {
            Monitorization.getInstance();
        } catch (NotInitializedException e) {
            throw new InstanceInitializeException(e);
        } catch (InvalidPropertiesException e) {
            throw new InstanceInitializeException(e);
        }
    }

    private static void initDistributed() throws InstanceInitializeException {
        //HASH
        TweetsProviderInstanceProvider.init();
        MongoHashConnectionInstanceProvider.init();
        UsersHashPersisProviderInstanceProvider.init();
        UsersWorkerHashCollectionInstanceProvider.init();

        try {
            DistributedMonitorization.getInstance();
        } catch (NotInitializedException e) {
            throw new InstanceInitializeException(e);
        } catch (InvalidPropertiesException e) {
            throw new InstanceInitializeException(e);
        }


        Properties properties = null;
        int numberOfInstancesOfUsersWorkerHashCollection;
        try {
            properties = PropertyFileLoader.getProperties();
            numberOfInstancesOfUsersWorkerHashCollection = Integer.parseInt(properties.getProperty("numberOfInstancesOfUsersWorkerHashCollection"));
        } catch (InvalidPropertiesException e) {
            throw new InstanceInitializeException(e);
        }

        ScheduledThreadPoolExecutor tpe = new ScheduledThreadPoolExecutor(10);
        for (int i = 0; i < numberOfInstancesOfUsersWorkerHashCollection; ++i) {
            tpe.execute(UsersWorkerHashCollectionInstanceProvider.getInstance());
        }
    }

    private static void setUpCommands() {
        CommandResolver.addCommand(new CommandMoveUsersCollection());
        CommandResolver.addCommand(new CommandExecuteStratex());
        CommandResolver.addCommand(new CommandExecuteDistributedStratex());
        CommandResolver.addCommand(new CommandRecycle());
        CommandResolver.addCommand(new CommandRefreshUsersList());
        CommandResolver.addCommand(new CommandStart());
        CommandResolver.addCommand(new CommandStatus());
        CommandResolver.addCommand(new CommandTimeout());
        CommandResolver.addCommand(new CommandUsersWorker());
    }

    private static void finalizer() {
        MongoConnectionInstanceProvider.finish();
        NetworkInterfaceInstanceProvider.finish();
        UsersWorkerInstanceProvider.finish();
    }

}
