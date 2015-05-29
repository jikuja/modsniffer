package io.github.jikuja.modsniffer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.codehaus.mojo.animal_sniffer.ClassListBuilder;
import org.codehaus.mojo.animal_sniffer.SignatureChecker;
import org.codehaus.mojo.animal_sniffer.logging.PrintWriterLogger;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ModSniffer {
    /**
     * @param args arguments
     * @throws Exception
     *
     * args[0] name of the signature file to use. e.g. "java16-1.1.signature"
     * args[1] input files. Directory, class file or jar. // TODO add support for multiple entries
     * args[2] list of packages to ignore. Does not report errors if input files use those packages.
     *         multiple packages can be separated with ; and * can be used as a wildcard
     *         e.g. "invtweaks.api.*;codechicken.nei.*;codechicken.lib.*"
     * args[3] list of directories/classes/jar to scan for ignore list. Really slow, recommended to use args[2]
     *         multiple directories/classes/jars can be separated with ;
     */
    public static void main( String[] args ) throws Exception
    {
        // hardcode libraries.
        // TODO: later add command line argument to disable those
        final String IGNORED_PACKAGES = "net.minecraft.*;cpw.mods.fml.*;net.minecraftforge.*;org.lwjgl.*;com.google.common.*;io.netty.*;org.objectweb.asm.*;org.apache.logging.*;com.mojang.*;org.apache.commons.*;gnu.trove.*;javax.vecmath.*;com.google.gson.*;scala.*;org.tukaani.xz.*;joptsimple.*;org.apache.bcel.*;paulscode.sound.*;ibxm.*;com.jcraft.*;org.apache.http.*";

        // setup args
        String signature = args[0];
        String inputFiles = args[1];
        Set<String> ignorePackagesManual = (args.length > 2 ? Sets.newHashSet(args[2].split(";")) : Sets.newHashSet(new String[0]));
        Collections.addAll(ignorePackagesManual, IGNORED_PACKAGES.split(";"));
        Set<String> ignorePackagesAuto = (args.length > 3 ? Sets.newHashSet(args[3].split(";")) : Sets.newHashSet(new String[0]));

        //print headers
        System.out.println("signature: " + signature);
        System.out.println("dir/jar/class: " + inputFiles);
        System.out.println("ignored packages: " + ignorePackagesManual);
        System.out.println("ignored jars/classes/zips: " + ignorePackagesAuto);
        System.out.println();

        System.out.println("Reading input files for ignore list...");
        ClassListBuilder plb = new ClassListBuilder(new PrintWriterLogger( System.out ));
        // scan classes from input files
        plb.process(new File(inputFiles));
        // scan classes from args[3]
        for (String i: ignorePackagesAuto) {
            plb.process(new File(i));
        }

        // merge ignore lists
        System.out.println("Setup ignorelist...");
        Set<String> ignoredPackages = new HashSet<String>();
        ignoredPackages.addAll(plb.getPackages());
        ignoredPackages.addAll(ignorePackagesManual);

        // initialize SignatureChecker
        System.out.println("Setup SignatureChecker...");
        SignatureChecker signatureChecker = new SignatureChecker( new FileInputStream(signature),
                ignoredPackages, new PrintWriterLogger( System.out ) );

        System.out.println("Applying fix into sourcePath...");
        signatureChecker.setSourcePath(Lists.newArrayList());

        // Start checking
        signatureChecker.process(new File(inputFiles));
    }
}
